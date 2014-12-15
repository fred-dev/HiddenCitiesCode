LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
# OpenCV
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=STATIC
include /Applications/adt-bundle-mac-x86_64-20140702/OpenCV-2.4.9-android-sdk/sdk/native/jni/OpenCV.mk
