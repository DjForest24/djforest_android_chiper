# DjForest Android Chiper

Android用データ暗号化/複合化ライブラリです。byte、Stringの他Bitmap、File、Audio等Androidで頻繁に使用されるデータを簡単な手順で暗号化/複合化できるようにしました。    
対応暗号タイプ :AES,Blowfish

## 使いかた

実装するには2つの方法があります。

### Jar File

1. [Download]よりjarファイルを取得し、Androidプロジェクトに実装してください。

### Android Library Project (SDK r6 or higher)

_以下はEclipseでの実装となります。_

[example]

インスタンスを生成します。
```java
AesChiper aes = new AesChiper("KEY_STRINGS");
```

関数を使い暗号化したい情報をセットします。

```java
String encrypted = aes.encryptToString("plainText");
```

以上の手順で[encrypted]には暗号化された情報がセットされます。
 
## LICENSE
Copyright &copy; 2012 DjForest
Licensed under the [Apache License, Version 2.0][Apache]
 
[Apache]: http://www.apache.org/licenses/LICENSE-2.0