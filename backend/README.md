# COLOR M@STER バックエンドモジュール

このモジュールは、COLOR M@STERアプリケーションのバックエンドサーバーを提供します。

## 機能

- REST APIエンドポイント
- SQLiteデータベース連携
- ビジネスロジック処理

## 技術スタック

- Ktor: サーバーフレームワーク
- SQLDelight: タイプセーフなSQLクエリ
- Kotlinx Serialization: JSONシリアライゼーション
- Kotlinx Coroutines: 非同期処理
- Logback: ロギング
- Docker: コンテナ化

## 開発方法

### ローカルでのサーバー起動

```bash
./gradlew :backend:run
