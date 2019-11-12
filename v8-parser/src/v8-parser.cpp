//============================================================================
// Name        : v8-parser.cpp
// Author      : qinbiao
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================

// g++ -L/home/lace/Documents/opensource/v8/out.gn/x64.release.sample/obj -lssl -shared -pthread -o "libv8v8-parser.so"  v8-parser.o   -lv8_monolith

#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <jni.h>

#include "include/v8.h"
#include "libplatform/libplatform.h"
#include "com_engine_jni_V8JNI.h"

#define MAX_RULE_FILE_SIZE 1073741824

using namespace std;

void Print(const v8::FunctionCallbackInfo<v8::Value>&);
const char* ToCString(const v8::String::Utf8Value&);
void Read(const v8::FunctionCallbackInfo<v8::Value>&);
void Load(const v8::FunctionCallbackInfo<v8::Value>&);
v8::MaybeLocal<v8::String> ReadFile(v8::Isolate*, const char*);
bool ExecuteString(v8::Isolate*, v8::Local<v8::String>, v8::Local<v8::Value>,
		bool, bool);
void ReportException(v8::Isolate*, v8::TryCatch*);
char * parse_rules(char * rules[] , jsize);
char * execV8interpreter(const char *);
JNIEXPORT jstring JNICALL Java_com_engine_jni_V8JNI_parseRules(JNIEnv *, jobject, jobjectArray);
JNIEXPORT jstring JNICALL Java_com_engine_jni_V8JNI_execInterpreter(JNIEnv *, jobject, jstring);

JNIEXPORT jstring JNICALL Java_com_engine_jni_V8JNI_parseRules
  (JNIEnv * env, jobject obj, jobjectArray files) {
	jsize count = env->GetArrayLength(files);
	char * rulePaths[count];
	for (int i = 0; i < count; i++) {
		jstring file = (jstring) env->GetObjectArrayElement(files, i);
		const char * file_path = env->GetStringUTFChars(file, 0);
		rulePaths[i] = (char *) malloc(strlen(file_path) * sizeof(char) + 1);
		strcpy(rulePaths[i], file_path);
	}
	char * result = parse_rules(rulePaths, count);
	return env->NewStringUTF(result);
}

JNIEXPORT jstring JNICALL Java_com_engine_jni_V8JNI_execInterpreter
  (JNIEnv * env, jobject obj, jstring code) {
	const char * source = env->GetStringUTFChars(code, NULL);
	printf("cstr: ----- %s\n", source);
//	char * exec_res = execV8interpreter(source); /// TODO: BUG POINT !!!! <-----
	return env->NewStringUTF(source);
//	return env->NewStringUTF("");
}

/**
 * 解析JS规则文件。
 */
char * parse_rules(char * rulePaths[], jsize count) {
	if (count <= 0) {
		return NULL;
	}
	// Initialize V8.
	v8::V8::InitializeICUDefaultLocation("js"); // or transmit parameter: argv[0]
	v8::V8::InitializeExternalStartupData("js"); // or transmit parameter: argv[0]
	std::unique_ptr<v8::Platform> platform = v8::platform::NewDefaultPlatform();
	v8::V8::InitializePlatform(platform.get());
	v8::V8::Initialize();

	// Create a new Isolate and make it the current one.
	v8::Isolate::CreateParams create_params;
	create_params.array_buffer_allocator =
			v8::ArrayBuffer::Allocator::NewDefaultAllocator();
	v8::Isolate *isolate = v8::Isolate::New(create_params);

	char * parsed_result = NULL;

	{
		v8::Isolate::Scope isolate_scope(isolate);
		// Create a stack-allocated handle scope.
		v8::HandleScope handle_scope(isolate);

		// 在JavaScript中添加Debug输出函数。
		v8::Local<v8::ObjectTemplate> global = v8::ObjectTemplate::New(isolate);
		global->Set(
				v8::String::NewFromUtf8(isolate, "print",
						v8::NewStringType::kNormal).ToLocalChecked(),
				v8::FunctionTemplate::New(isolate, Print));
		global->Set(
				v8::String::NewFromUtf8(isolate, "load",
						v8::NewStringType::kNormal).ToLocalChecked(),
				v8::FunctionTemplate::New(isolate, Load));

		// Create a new context.
		v8::Local<v8::Context> context = v8::Context::New(isolate, NULL,
				global);
		// Enter the context from compiling and running the JavaScript.
		v8::Context::Scope context_scope(context);

		v8::Local<v8::String> source;
		v8::Local<v8::Script> script;
		v8::Local<v8::Value> result;

		// TODO: 提前加载JS规则的公有脚本！！！
		source = ReadFile(isolate, "/home/lace/Documents/workspace-sts-3.9.10.RELEASE/IAST-Agent-develop/Engine/init/base.js").ToLocalChecked();
		script = v8::Script::Compile(context, source).ToLocalChecked();
		result = script->Run(context).ToLocalChecked();
		/* 加载定义的JS规则脚本 */
		for (int i = 0; i < count; i++) {
			source = ReadFile(isolate, rulePaths[i]).ToLocalChecked();
			v8::String::Utf8Value utf8(isolate, source);
			script = v8::Script::Compile(context, source).ToLocalChecked();
			result = script->Run(context).ToLocalChecked();
		}

		// 保存v8解析所有JS规则脚本后的返回结果
		parsed_result = (char *) malloc(strlen(*v8::String::Utf8Value(isolate, result)) * sizeof(char) + 1);
		strcpy(parsed_result, *v8::String::Utf8Value(isolate, result));
	}
	// Dispose the isolate and tear down V8.
	isolate->Dispose();
	v8::V8::Dispose();
	v8::V8::ShutdownPlatform();
	delete create_params.array_buffer_allocator;

	return parsed_result;
}

/**
 * 执行V8解释器。
 */
char * execV8interpreter(const char * code) {
	// Initialize V8.
	v8::V8::InitializeICUDefaultLocation("js"); // or transmit parameter: argv[0]
	v8::V8::InitializeExternalStartupData("js"); // or transmit parameter: argv[0]
	std::unique_ptr<v8::Platform> platform = v8::platform::NewDefaultPlatform();
	v8::V8::InitializePlatform(platform.get());
	v8::V8::Initialize();

	// Create a new Isolate and make it the current one.
	v8::Isolate::CreateParams create_params;
	create_params.array_buffer_allocator =
			v8::ArrayBuffer::Allocator::NewDefaultAllocator();
	v8::Isolate *isolate = v8::Isolate::New(create_params);

	char * result_str = NULL;
	{
		v8::Isolate::Scope isolate_scope(isolate);
		// Create a stack-allocated handle scope.
		v8::HandleScope handle_scope(isolate);
		// 在JavaScript中添加Debug输出函数。
		v8::Local<v8::ObjectTemplate> global = v8::ObjectTemplate::New(isolate);
		global->Set(
				v8::String::NewFromUtf8(isolate, "print",
						v8::NewStringType::kNormal).ToLocalChecked(),
				v8::FunctionTemplate::New(isolate, Print));
		global->Set(
				v8::String::NewFromUtf8(isolate, "load",
						v8::NewStringType::kNormal).ToLocalChecked(),
				v8::FunctionTemplate::New(isolate, Load));

		// Create a new context.
		v8::Local<v8::Context> context = v8::Context::New(isolate, NULL,
				global);
		// Enter the context from compiling and running the JavaScript.
		v8::Context::Scope context_scope(context);

		v8::Local<v8::String> source = v8::String::NewFromUtf8(isolate, code, v8::NewStringType::kNormal).ToLocalChecked();
		v8::Local<v8::Script> script = v8::Script::Compile(context, source).ToLocalChecked();
		v8::Local<v8::Value> result = script->Run(context).ToLocalChecked();
		result_str = (char *) malloc(strlen(*v8::String::Utf8Value(isolate, result)) * sizeof(char) + 1);
		strcpy(result_str, *v8::String::Utf8Value(isolate, result));
	}

	// Dispose the isolate and tear down V8.
	isolate->Dispose();
	v8::V8::Dispose();
	v8::V8::ShutdownPlatform();
	delete create_params.array_buffer_allocator;
	return result_str;
}

int main() {
	cout << "!!!Hello World!!!" << endl; // prints !!!Hello World!!!
	return 0;
}


/**
 * 定义在JS规则中可调用的输出函数，在JS规则中调用格式是：print("");
 */
void Print(const v8::FunctionCallbackInfo<v8::Value> &args) {
	bool first = true;
	for (int i = 0; i < args.Length(); i++) {
		v8::HandleScope handle_scope(args.GetIsolate());
		if (first) {
			first = false;
		} else {
			printf(" ");
		}
		v8::String::Utf8Value str(args.GetIsolate(), args[i]);
		const char *cstr = ToCString(str);
		printf("%s", cstr);
	}
	printf("\n");
	fflush(stdout);
}

const char* ToCString(const v8::String::Utf8Value &value) {
	return *value ? *value : "<string conversion failed>";
}

// The callback that is invoked by v8 whenever the JavaScript 'read'
// function is called.  This function loads the content of the file named in
// the argument into a JavaScript string.
void Read(const v8::FunctionCallbackInfo<v8::Value> &args) {
	if (args.Length() != 1) {
		args.GetIsolate()->ThrowException(
				v8::String::NewFromUtf8(args.GetIsolate(), "Bad parameters",
						v8::NewStringType::kNormal).ToLocalChecked());
		return;
	}
	v8::String::Utf8Value file(args.GetIsolate(), args[0]);
	if (*file == NULL) {
		args.GetIsolate()->ThrowException(
				v8::String::NewFromUtf8(args.GetIsolate(), "Error loading file",
						v8::NewStringType::kNormal).ToLocalChecked());
		return;
	}
	v8::Local<v8::String> source;
	if (!ReadFile(args.GetIsolate(), *file).ToLocal(&source)) {
		args.GetIsolate()->ThrowException(
				v8::String::NewFromUtf8(args.GetIsolate(), "Error loading file",
						v8::NewStringType::kNormal).ToLocalChecked());
		return;
	}

	args.GetReturnValue().Set(source);
}

// The callback that is invoked by v8 whenever the JavaScript 'load'
// function is called.  Loads, compiles and executes its argument
// JavaScript file.
void Load(const v8::FunctionCallbackInfo<v8::Value> &args) {
	for (int i = 0; i < args.Length(); i++) {
		v8::HandleScope handle_scope(args.GetIsolate());
		v8::String::Utf8Value file(args.GetIsolate(), args[i]);
		if (*file == NULL) {
			args.GetIsolate()->ThrowException(
					v8::String::NewFromUtf8(args.GetIsolate(),
							"Error loading file", v8::NewStringType::kNormal).ToLocalChecked());
			return;
		}
		v8::Local<v8::String> source;
		if (!ReadFile(args.GetIsolate(), *file).ToLocal(&source)) {
			args.GetIsolate()->ThrowException(
					v8::String::NewFromUtf8(args.GetIsolate(),
							"Error loading file", v8::NewStringType::kNormal).ToLocalChecked());
			return;
		}
		if (!ExecuteString(args.GetIsolate(), source, args[i], false, false)) {
			args.GetIsolate()->ThrowException(
					v8::String::NewFromUtf8(args.GetIsolate(),
							"Error executing file", v8::NewStringType::kNormal).ToLocalChecked());
			return;
		}
	}
}

// Reads a file into a v8 string.
v8::MaybeLocal<v8::String> ReadFile(v8::Isolate *isolate, const char *name) {
	FILE *file = fopen(name, "rb");
	if (file == NULL)
		return v8::MaybeLocal<v8::String>();

	fseek(file, 0, SEEK_END);
	size_t size = ftell(file);
	rewind(file);

	char *chars = new char[size + 1];
	chars[size] = '\0';
	for (size_t i = 0; i < size;) {
		i += fread(&chars[i], 1, size - i, file);
		if (ferror(file)) {
			fclose(file);
			return v8::MaybeLocal<v8::String>();
		}
	}
	fclose(file);
	v8::MaybeLocal<v8::String> result = v8::String::NewFromUtf8(isolate, chars,
			v8::NewStringType::kNormal, static_cast<int>(size));
	delete[] chars;
	return result;
}

// Executes a string within the current v8 context.
bool ExecuteString(v8::Isolate *isolate, v8::Local<v8::String> source,
		v8::Local<v8::Value> name, bool print_result, bool report_exceptions) {
	v8::HandleScope handle_scope(isolate);
	v8::TryCatch try_catch(isolate);
	v8::ScriptOrigin origin(name);
	v8::Local<v8::Context> context(isolate->GetCurrentContext());
	v8::Local<v8::Script> script;
	if (!v8::Script::Compile(context, source, &origin).ToLocal(&script)) {
		// Print errors that happened during compilation.
		if (report_exceptions)
			ReportException(isolate, &try_catch);
		return false;
	} else {
		v8::Local<v8::Value> result;
		if (!script->Run(context).ToLocal(&result)) {
			assert(try_catch.HasCaught());
			// Print errors that happened during execution.
			if (report_exceptions)
				ReportException(isolate, &try_catch);
			return false;
		} else {
			assert(!try_catch.HasCaught());
			if (print_result && !result->IsUndefined()) {
				// If all went well and the result wasn't undefined then print
				// the returned value.
				v8::String::Utf8Value str(isolate, result);
				const char *cstr = ToCString(str);
				printf("%s\n", cstr);
			}
			return true;
		}
	}
}

void ReportException(v8::Isolate *isolate, v8::TryCatch *try_catch) {
	v8::HandleScope handle_scope(isolate);
	v8::String::Utf8Value exception(isolate, try_catch->Exception());
	const char *exception_string = ToCString(exception);
	v8::Local<v8::Message> message = try_catch->Message();
	if (message.IsEmpty()) {
		// V8 didn't provide any extra information about this error; just
		// print the exception.
		fprintf(stderr, "%s\n", exception_string);
	} else {
		// Print (filename):(line number): (message).
		v8::String::Utf8Value filename(isolate,
				message->GetScriptOrigin().ResourceName());
		v8::Local<v8::Context> context(isolate->GetCurrentContext());
		const char *filename_string = ToCString(filename);
		int linenum = message->GetLineNumber(context).FromJust();
		fprintf(stderr, "%s:%i: %s\n", filename_string, linenum,
				exception_string);
		// Print line of source code.
		v8::String::Utf8Value sourceline(isolate,
				message->GetSourceLine(context).ToLocalChecked());
		const char *sourceline_string = ToCString(sourceline);
		fprintf(stderr, "%s\n", sourceline_string);
		// Print wavy underline (GetUnderline is deprecated).
		int start = message->GetStartColumn(context).FromJust();
		for (int i = 0; i < start; i++) {
			fprintf(stderr, " ");
		}
		int end = message->GetEndColumn(context).FromJust();
		for (int i = start; i < end; i++) {
			fprintf(stderr, "^");
		}
		fprintf(stderr, "\n");
		v8::Local<v8::Value> stack_trace_string;
		if (try_catch->StackTrace(context).ToLocal(&stack_trace_string)
				&& stack_trace_string->IsString()
				&& v8::Local<v8::String>::Cast(stack_trace_string)->Length()
						> 0) {
			v8::String::Utf8Value stack_trace(isolate, stack_trace_string);
			const char *stack_trace_string = ToCString(stack_trace);
			fprintf(stderr, "%s\n", stack_trace_string);
		}
	}
}
