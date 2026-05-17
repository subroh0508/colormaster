---
id: rules-markdown
title: Markdown 表記規約
status: stable
last_updated: 2026-05-17
paths:
  - "**/*.md"
related_adrs:
  - ADR-0027
---

# markdown.md — Markdown 表記規約

> 本リポジトリの全 Markdown (`docs/**` / `.claude/rules/**` / `.github/**` / `*.md`) に
> 適用する表記規約。**日本語化**は `template-language.md` を Single Source of Truth とし、
> 本 rule は CommonMark / GFM の機械検証可能な表記ルールに集中する。

## 見出し

- `#` (h1) は **ファイル冒頭の 1 つのみ** (frontmatter の後)
- `##` (h2) からは複数可、レベル飛ばし禁止 (`#` → `###` は NG)
- 末尾コロン / ピリオド禁止、識別子 (SPEC-NNN-N) はそのまま
- **日本語必須** (`template-language.md` 参照、ADR 0027)、code 識別子 / 英語固有名詞は OK

## frontmatter

- YAML frontmatter は **ファイル先頭** (`---` で開閉)
- 配列は **block 形式必須** (`docs-structure.md` frontmatter 規約):

  ```yaml
  related_adrs:
    - ADR-0001
    - ADR-0011
  ```

- flow 形式 `[A, B]` は禁止 (機械検証で reject、A6)
- 必須キーは docs 種別ごとに定義 (`docs-structure.md` §frontmatter 必須キー表 参照)

## リンク

- 外部 URL: `[label](https://...)` の通常リンク (タイトル属性不要)
- 内部リンク: ファイル相対パス (`[label](../adr/ADR-0001-adr-charter.md)`) を推奨
- 識別子参照: `ADR-0001` / `EPIC-NNN` / `SPEC-NNN-N` は **裸書き** (リンクなし) で OK、必要時のみリンク化

## code block

- 言語指定必須 (` ```kotlin ` / ` ```sql ` / ` ```text `)
- 言語不明 / プレーンテキストは ` ```text ` を明示 (mt は不可)
- 行数は **30 行以内** を目安、超える場合はファイル参照 (`file_path:line` 形式) に置換

## 表

- 列ヘッダ必須 (`| col1 | col2 |` + `|---|---|`)
- 列幅は max ~3 列推奨、4 列以上は別の表現 (リスト / 別表) を検討
- 表中の改行は `<br/>` で明示 (CommonMark の table extension で標準)

## list

- 順序なしは `-`、順序ありは `1.` (Markdown auto-renumbering に任せる、明示番号も OK)
- ネストは **半角スペース 2 つ** インデント
- 1 行で長くなる場合は `- foo:` + 改行 + インデント + 説明文

## 強調

- 強調: `**bold**` (`__bold__` 禁止)
- 斜体: `*italic*` (`_italic_` 禁止)
- 取り消し線: `~~strikethrough~~` (使う場面少)
- code: `` `inline_code` ``

## 識別子・パス・コード

- パス / ファイル名: `` `core/data/Repository.kt` `` (inline code)
- 識別子: `ADR-0001` / `SPEC-IDOL-001-3` (裸書き可、code wrap も可)
- コード断片: 設計書本文 (`docs/{requirements,specifications}/**`) には **コードを書かない** (§4.6 のコード禁止原則)、`file_path:line` のみ許容

## blockquote

- 注意書き / 引用: `>`
- 多段引用は `>>` でネスト (CommonMark の table-extension に従う)

## 機械検証 (A6 で導入)

- **markdownlint-cli2** + 設定ファイル (`.markdownlint-cli2.jsonc`、A6 で配置) で以下を検証:
  - 見出しレベル飛ばし禁止 (MD001)
  - 行末 trailing space (MD009)
  - 同一見出し重複禁止 (MD024)
  - リンク URL 検証 (MD034 / MD039)
  - フェンス言語指定必須 (MD040)
- **Gradle カスタムタスク** で frontmatter / 5 行 summary / 日本語見出しを検証 (`docs-structure.md` §機械検証 参照)
- Konsist は Kotlin file 専用のため Markdown 検証には使えない (§5.2)

## Gotchas

- **`<br>` / `<br/>` の混在**: GFM では `<br/>` を推奨 (XHTML 互換)
- **table 内の `|` エスケープ**: `\|` で明示
- **行末空白 2 つで改行** は GFM 標準だが、可読性のため明示的に `<br/>` を推奨
- **`---` の用途**: frontmatter 開閉 / 水平線で同じ syntax、frontmatter は **ファイル先頭** のみ
- 日本語 / 英語混在時、半角スペース挿入の有無で見た目が変わる: 「ADR 0001 を参照」は半角スペース、「`ADR-0001`を参照」は不要 (識別子は inline code 扱い)

## 関連

- ADR 0027 (docs 構造 + 日本語化方針)
- CommonMark: https://commonmark.org/
- GFM: https://github.github.com/gfm/
- `.claude/rules/{template-language,docs-structure}.md`
- `.markdownlint-cli2.jsonc` (A6 で配置)
