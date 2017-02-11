# SilicaGel-for-CloudPlayer
CloudPlayerの通知から曲の情報を取得し、今再生している曲についてツイートします。

## How to Use
1. ビルドするか、APKファイルをダウンロードしてインストールします。（要開発者モード）
2. 起動します。通知へのアクセスが許可されてなければ自動で設定画面を表示します。
3. Twitterの認証をタップして、認証を行います。
4. お好みに合わせて設定してください。
5. CloudPlayerで音楽を再生すると、NowPlayingツイートが行われます。

## Notes
- このソフトウェアを利用したことでいかなる損害が発生しても、製作者は責任を負いません。
- このソフトウェアはdoubleTwist社のCloudPlayer向けに製作しています。

## Required Libraries
- Twitter4j Core 4.0.4
- Twitter4j Media Support 4.0.4
（ビルド時のみ。APKからのインストールには必要ありません。）

## Building
app/src/main/res/values/strings.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="consumer_key"><!-- Twitter Consumer Key --></string>
    <string name="consumer_secret"><!-- Twitter Consumer Secret --></string>
</resources>
```

## Compatibility for Other Player
プレイヤーによっては、NotificationService.java内のPACKAGE_FILTERフィールドを変更するだけで動くかもしれません。
動かない場合は適当にソースコードを改変してみてください。

## License
このソフトウェアはMITライセンスでリリースされています。
詳しくは、LICENSE.mdを参照してください。
