
LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_STATIC_JAVA_LIBRARIES := \
    android-support-v4 \
    android-support-v7-recyclerview \
	
LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := SavePowerHelper

LOCAL_CERTIFICATE := platform

ifeq ("$(PRODUCT_MODEL)","a_10f_zh")
  LOCAL_MANIFEST_FILE := a_10f_zh/AndroidManifest.xml
endif

include $(BUILD_PACKAGE)

include $(call all-makefiles-under,$(LOCAL_PATH))

