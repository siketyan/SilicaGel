# SilicaGel
CloudPlayerの通知から曲の情報を取得し、今再生している曲についてツイートします。  

[![Try it on your device with DeployGate](https://dply.me/nr6yvg/button/large)](https://dply.me/nr6yvg#install)
  
## How to Use
1. ビルドするか、APKファイルをダウンロードしてインストールします。（「開発元不明のアプリ」を許可する必要があります。）
2. 起動します。通知へのアクセスが許可されてなければ自動で設定画面を表示します。
3. Twitterの認証をタップして、認証を行います。
4. お好みに合わせて設定してください。
5. CloudPlayerで音楽を再生すると、NowPlayingツイートが行われます。

## Notes
- このソフトウェアを利用したことでいかなる損害が発生しても、製作者は責任を負いません。

## Compatible Social Services
- Twitter
- Mastodon

## Compatible Players
- doubleTwist CloudPlayer
- Google Play Music
- Spotify
- Amazon Music
- Sony Music (%album% unsupported)
- ANiUTa
- SoundCloud (%album% unsupported)
- AIMP (%album% and album arts unsupported, %artist% will be the format of the file)

## Required Libraries
- Twitter4j Core 4.0.4
- Twitter4j Media Support 4.0.4

（ビルド時のみ。APKからのインストールには必要ありません。）

## Building
app/src/main/java/me/siketyan/silicagel/util/TwitterApi.java
```java
package me.siketyan.silicagel.util;

public class TwitterApi {
    static final String CONSUMER_KEY = ""; // YOUR CONSUMER KEY
    static final String CONSUMER_SECRET = ""; // YOUR CONSUMER SECRET
}
```

## License
このソフトウェアはMITライセンスでリリースされています。
詳しくは、LICENSE.mdを参照してください。
