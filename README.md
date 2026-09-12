# ساختمان من

اپ مدیریت ساختمان به زبان فارسی، Kotlin و Jetpack Compose (Material 3، RTL، minSdk 24).

## دانلود APK

بعد از هر push روی `main`، GitHub Actions فایل APK را می‌سازد:

1. برو به [Actions](https://github.com/h92727567-max/sakhteman-man/actions)
2. آخرین اجرای **Build APK** را باز کن
3. از بخش **Artifacts** فایل `sakhteman-man-debug` را دانلود کن
4. روی گوشی نصب کن (منبع ناشناس را موقتاً اجازه بده)

ورود آزمایشی: شماره موبایل + کد تایید `1234`

## امکانات نسخه ۱

- ورود با نقش مدیر / مالک / مستأجر / نگهبان
- داشبورد صندوق و بدهکاران
- شارژ با روش مساوی، متراژ، ترکیبی، سفارشی
- واحدها، هزینه‌ها، تعمیرات با پیشنهاد هوشمند
- اعلانات، رأی‌گیری، مراجعین، بسته، پارکینگ، کارکنان، گزارش

## ساخت محلی

Android Studio Hedgehog+ یا JDK 17:

```bash
gradle :app:assembleDebug
```

خروجی: `app/build/outputs/apk/debug/app-debug.apk`
