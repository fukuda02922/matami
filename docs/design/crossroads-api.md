# 岐路 CRUD API 仕様

## エンドポイント一覧

| Method | Path | 認証 | 説明 |
|--------|------|------|------|
| GET | /v1/crossroads | 不要 | フィード取得 |
| POST | /v1/crossroads | 必須 | 岐路投稿 |
| GET | /v1/crossroads/{id} | 不要 | 詳細取得 |
| GET | /v1/me/crossroads | 必須 | マイ岐路一覧 |

---

## GET /v1/crossroads

フィードをカーソルページネーションで取得する。

### クエリパラメータ

| 名前 | 型 | 必須 | デフォルト | 説明 |
|------|----|------|-----------|------|
| theme | string | No | - | CAREER / LOVE / EDUCATION / FAMILY / MONEY / OTHER |
| cursor | string | No | - | 次ページカーソル（Base64 エンコード済み） |
| limit | int | No | 20 | 1〜100 |

### レスポンス例

```json
{
  "items": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "theme": "CAREER",
      "body": "30歳で外資系に転職するか悩んだ",
      "choiceA": "転職する",
      "choiceB": "残る",
      "chosen": "A",
      "ageAtTime": 30,
      "countA": 42,
      "countB": 17,
      "resonanceCount": 59,
      "createdAt": "2026-01-15T10:30:00Z"
    }
  ],
  "nextCursor": "MTcwMDAwMDAwMC4wOjU1MGU4NDAwLWUyOWItNDFkNC1hNzE2LTQ0NjY1NTQ0MDAwMA=="
}
```

---

## POST /v1/crossroads

### リクエストボディ

```json
{
  "theme": "CAREER",
  "body": "30歳で外資系に転職するか悩んだ（最大140文字）",
  "choiceA": "転職する（最大30文字）",
  "choiceB": "残る（最大30文字）",
  "chosen": "A",
  "ageAtTime": 30
}
```

### バリデーション

| フィールド | 制約 |
|-----------|------|
| theme | 必須、Theme enum 値 |
| body | 必須、最大140文字 |
| choiceA | 必須、最大30文字 |
| choiceB | 必須、最大30文字 |
| chosen | 必須、"A" または "B" |
| ageAtTime | 任意、10〜100 |

### レスポンス

201 Created — CrossroadsResponse（上記と同じ形式）

---

## GET /v1/crossroads/{id}

### レスポンス

200 OK — CrossroadsResponse

404 Not Found — 該当 ID が存在しない場合

---

## GET /v1/me/crossroads

### レスポンス

200 OK — CrossroadsResponse の配列（createdAt 降順）

---

## カーソルページネーション仕様

### カーソルの構造

```
Base64UrlEncode("{epochSecond}.{nano}:{uuid}")
```

例: `1700000000.0:550e8400-e29b-41d4-a716-446655440000`

### ページング動作

1. クライアントは最初のリクエストで `cursor` を省略する
2. レスポンスの `nextCursor` が `null` の場合は最終ページ
3. 次ページ取得時は `cursor=<nextCursor値>` をクエリパラメータに付与する
4. サーバーは `createdAt DESC, id DESC` 順で `limit + 1` 件取得し、超過分を切り捨てて `nextCursor` を生成する

---

## 認証フロー

```
クライアント                     API サーバー              Supabase
    |                               |                       |
    |-- POST /auth/... ------------>|                       |
    |<-- Supabase JWT --------------|                       |
    |                               |                       |
    |-- POST /v1/crossroads ------->|                       |
    |   Authorization: Bearer <JWT> |                       |
    |                               |-- JWT 検証 (HS256) -->|
    |                               |   (SUPABASE_JWT_SECRET)|
    |                               |<-- 検証結果 ----------|
    |                               |                       |
    |                               |  sub クレームから     |
    |                               |  supabase_uid を取得  |
    |                               |  → Spring Security の |
    |                               |    Authentication に  |
    |                               |    UUID としてセット   |
    |<-- 201 Created ---------------|                       |
```

### 公開エンドポイント（認証不要）

- `GET /v1/crossroads`
- `GET /v1/crossroads/{id}`
- `GET /v1/health`
- `GET /actuator/**`
- `GET /swagger-ui/**`
- `GET /v3/api-docs/**`

### 認証必須エンドポイント

上記以外の全エンドポイント。JWT が無効または欠落している場合は 403 Forbidden を返す。

### JWT 検証ライブラリ

`com.nimbusds:nimbus-jose-jwt:9.37.3` を使用。アルゴリズムは HS256。

環境変数 `SUPABASE_JWT_SECRET` に Supabase プロジェクトの JWT シークレットを設定する。
