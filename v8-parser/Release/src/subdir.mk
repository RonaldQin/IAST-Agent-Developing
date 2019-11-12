################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/v8-parser.cpp 

OBJS += \
./src/v8-parser.o 

CPP_DEPS += \
./src/v8-parser.d 


# Each subdirectory must supply rules for building sources it contributes
src/%.o: ../src/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -I/opt/jdk1.8.0_221/include -I/opt/jdk1.8.0_221/include/linux -I/home/lace/Documents/opensource/v8 -I/home/lace/Documents/opensource/v8/include -I"/home/lace/Documents/workspace-sts-3.9.10.RELEASE/IAST-Agent-develop/Engine/jni" -O3 -Wall -c -fmessage-length=0 -fPIC -pthread -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


