# また道 — 設計概要

## コンセプト

「あのとき別の道を選んでいたら」という普遍的な感情を、匿名の集合知に変えるアプリ。
ユーザーは自分の岐路（選択）を投稿し、同じ選択をした人の「その後」を読むことで、現在の悩みに対するリアルな参考情報を得られる。

## ターゲットユーザー

- **主要ペルソナ**: 25〜35歳、人生の節目（転職・恋愛・進路）で悩んでいる人
- 匿名なので本音を書ける、かつ同世代のリアルな経験談を求めている

## コア機能（MVP）

| # | 機能 | 説明 |
|---|------|------|
| 1 | ホームフィード | 岐路カードの一覧。テーマフィルター付き |
| 2 | 岐路投稿 | 年齢・テーマ・内容・A/B選択肢・その後を入力 |
| 3 | 共鳴タイムライン | 似た選択をした人の「その後」を時系列表示 |
| 4 | マイ岐路 | 自分の投稿一覧と共鳴数の確認 |
| 5 | パターンレポート ★ | AI による決断傾向の可視化（月 480円） |
| 6 | 岐路コーチ ★ | 今の悩みに似た経験者をAI検索（月 980円） |

★ = 有料機能

## データモデル（概要）

```
User
  id, supabase_uid, age_range, created_at

Crossroads（岐路）
  id, user_id, theme, body, choice_a, choice_b, chosen, age_at_time
  embedding vector(1536), created_at

Resonance（共鳴・その後）
  id, crossroads_id, user_id, choice, after_text, years_later, created_at

Reaction
  id, crossroads_id, user_id, type(helpful/same/different), created_at
```

## 収益モデル

| プラン | 価格 | 内容 |
|--------|------|------|
| 無料 | ¥0 | 投稿・閲覧・共鳴（全機能の基本） |
| 振り返りプラン | ¥480/月 | パターンレポート（月次AI分析） |
| 岐路コーチプラン | ¥980/月 | パターンレポート + 岐路コーチ |

## 参照ドキュメント

- [マイルストーン](./milestone.md)
- [画面仕様](./screens/)
- [APIエンドポイント](../api/openapi.yaml)
- [インフラ設計](../infrastructure/architecture.md)
