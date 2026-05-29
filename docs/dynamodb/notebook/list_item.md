# List Item Data Model

## Categories

- `CATEGORY`, `ONLY_TITLE`: Single ListItem record
- `LINK`, `TEXT`: Single ListItem record, url and content is in `data`
- `CHECKLIST`
    - `ListItem`
    - `ChecklistItem` for each entry (individual access)
    - `Content`
- `IMAGE`, `FILE`: Single ListItem record, storedFileId in `data`
- `TABLE`, `CHECKLIST_TABLE`, `CUSTOM_TABLE`
    - `ListItem`
    - `TableHead`
    - `TableRow` for each row
        - `Columns`
    - `Content` (`TableHead` and `Column` data)

## ListItem

- `pk`: `USER#userId`
- `sk`: `LIST_ITEM#listItemId`
- `parent`: `LIST_ITEM#(listItemId`) - listItemId is empty if parent is root, LIST_ITEM# prefix is still present
- `type`: `enum: ListItemType`
- `title`: `text` (encrypted)
- `pinned`: `boolean` (encrypted)
- `archived`: `boolean` (encrypted)
- `data`: `storedFileId/url/text content` (encrypted)

### GSI-pk-parent (Get children of a parent)

- `pk`: `USER#userId`
- `sk`: `parent`

### GSI-pk-type (Get all Categories)

- `pk`: `USER#userId`
- `sk`: `type`

## Content

- `pk`: `LIST_ITEM#listItemId`
- `sk`: `CONTENT#batchIndex`
- `content`: `Map<uuid, text>` (serialized to JSON string and encrypted)

## ChecklistItem

- `pk`: `LIST_ITEM#listItemId`
- `sk`: `CHECKLIST_ITEM#checklistItemId`
- `index`: `number` (encrypted)
- `checked`: `boolean` (encrypted)

## TableHead

- `pk`: `LIST_ITEM#listItemId`
- `sk`: `TABLE_HEAD`
- `data`: `List<TableHead>` (serialized to JSON string and encrypted)
  - `tableHeadId`: uuid (contentKey)
  - `index`: number

## TableRow

- `pk`: `LIST_ITEM#listItemId`
- `sk`: `TABLE_ROW#tableRowId`
- `index`: `number` (encrypted)
- `checked`: `boolean` (encrypted)
- `columns`: `List<TableColumn>` (serialized to JSON string and encrypted)
    - `columnId`: `uuid` contentKey
    - `index`: `number`
    - `type`: `ColumnType`

