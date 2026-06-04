# また道（Matamichi）

> 「あのとき別の道を選んでいたら」— 人生の岐路を共有し、同じ選択をした人の「その後」を知るアプリ

## ドキュメント

- [設計概要](docs/design/overview.md)
- [マイルストーン](docs/design/milestone.md)
- [API仕様](docs/api/openapi.yaml)
- [インフラ設計](docs/infrastructure/architecture.md)
- [開発ガイド](CLAUDE.md)

## クイックスタート

```bash
# Mobile
cd apps/mobile && npx expo start

# Backend
cd apps/backend && ./gradlew bootRun

# Local DB
docker compose up -d
```
