@echo off
chcp 65001 >nul
echo ===================================================
echo     بناء تطبيق مواقيت الصلاة لشاشات Android TV
echo ===================================================
echo.

set "JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"

if not exist "%JAVA_HOME%\bin\java.exe" (
    echo [خطأ] لم يتم العثور على Java JDK 17 في المسار الافتراضي!
    pause
    exit /b 1
)

echo [1/3] فحص ملفات التطبيق والموارد...
if not exist "app\src\main\assets\index.html" (
    echo [خطأ] ملف index.html غير موجود في assets!
    pause
    exit /b 1
)

echo [2/3] تحديث وتجهيز الموارد...
echo جاري تجميع التطبيق بصيغة APK...
echo.

if exist "gradlew.bat" (
    call gradlew.bat assembleRelease
) else if exist "%USERPROFILE%\gradle\bin\gradle.bat" (
    call "%USERPROFILE%\gradle\bin\gradle.bat" assembleRelease
) else (
    echo جاري استخدام Gradle المتاح في النظام...
    call gradle assembleRelease
)

if exist "app\build\outputs\apk\release\app-release-unsigned.apk" (
    echo.
    echo ===================================================
    echo    تم بناء التطبيق بنجاح!
    echo    مسار الملف: app\build\outputs\apk\release\app-release-unsigned.apk
    echo ===================================================
) else if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo.
    echo ===================================================
    echo    تم بناء التطبيق بنجاح!
    echo    مسار الملف: app\build\outputs\apk\debug\app-debug.apk
    echo ===================================================
) else (
    echo [تنبيه] يمكنك فتح هذا المجلد مباشرة في Android Studio والضغط على Run أو Build APK.
)

pause
