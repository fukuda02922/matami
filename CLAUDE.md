# また道（Matamichi）— Claude Code 開発ガイド

## プロジェクト概要

「また道」は人生の岐路（転職・恋愛・進路など）を匿名で投稿し、同じ選択をした人の「その後」を共鳴タイムラインで見られるソーシャルアプリ。

**技術スタック**
- Mobile: React Native (Expo SDK 51)
- Backend: Spring Boot 3.x (Kotlin)
- DB: PostgreSQL 16 + pgvector (Supabase)
- Cache: Redis (Upstash)
- Auth: Supabase Auth (Apple/Google)
- AI: OpenAI API (gpt-4o-mini, text-embedding-3-small, gpt-4o)
- Infra: Railway (MVP期) → AWS ECS (スケール期)
- In-app purchases: RevenueCat

## リポジトリ構成

```
matami/
├── apps/
│   ├── mobile/          # Expo React Native アプリ
│   └── backend/         # Spring Boot API
├── docs/
│   ├── design/          # 画面設計・仕様 (Markdown)
│   ├── api/             # API仕様 (OpenAPI)
│   └── infrastructure/  # インフラ設計
├── CLAUDE.md            # このファイル
└── README.md
```

## 開発ルール

### ドキュメント方針
- **設計ドキュメントは必ずコードと同時に作成する**
- `docs/design/` に各機能の仕様を Markdown で記述
- `docs/api/` に OpenAPI 3.0 仕様を記述
- コードにコメントは原則不要。WHY が非自明な場合のみ一行で記述

### テスト方針
- **テストコードはプロダクションコードと同時に作成する**
- Backend: JUnit 5 + MockK + Testcontainers
- Mobile: Jest + React Native Testing Library
- カバレッジ目標: ビジネスロジック 80%以上

### コーディング規約
- Backend (Kotlin): ktlint 準拠
- Mobile (TypeScript): ESLint + Prettier
- コミットメッセージ: `feat:`, `fix:`, `docs:`, `test:`, `refactor:` プレフィックス

### セキュリティ
- 環境変数は `.env.local` に記述（`.gitignore` 対象）
- API キーをコードにハードコードしない
- 全エンドポイントで認証必須（公開フィードは例外）

## フェーズ管理

| Phase | 内容 | 期間 |
|-------|------|------|
| Phase 0 | 市場検証 | Week 1–2 |
| Phase 1 | MVP開発 | Week 3–10 |
| Phase 2 | グロース | Week 11–18 |
| Phase 3 | マネタイズ | Week 19〜 |

詳細: `docs/design/milestone.md`

## よく使うコマンド

### Mobile
```bash
cd apps/mobile
npx expo start           # 開発サーバー起動
npx expo run:ios         # iOS シミュレータ
npx jest                 # テスト実行
npx jest --coverage      # カバレッジ付きテスト
```

### Backend
```bash
cd apps/backend
./gradlew bootRun        # 開発サーバー起動
./gradlew test           # テスト実行
./gradlew ktlintCheck    # Lint チェック
./gradlew build          # ビルド
```

### Docker (ローカル DB)
```bash
docker compose up -d     # PostgreSQL + Redis 起動
docker compose down      # 停止
```

## 環境変数

`.env.local` に以下を設定（`.env.example` を参照）:

```
SUPABASE_URL=
SUPABASE_ANON_KEY=
SUPABASE_SERVICE_ROLE_KEY=
OPENAI_API_KEY=
REDIS_URL=
REVENUECAT_PUBLIC_KEY=
```
