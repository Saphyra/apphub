# `apphub-${Environment}-calendar`

# Event
- `pk`: `USER#userId`
- `sk`: `EVENT#eventId`
- `repetitionType`: `RepetitionType` (encrypted)
- `repetitionData`: `object` (encrypted)
- `startDate`: `LocalDate` (encrypted)
- `endDate`: `LocalDate` (encrypted)
- `time`: `LocalTime` (encrypted)
- `title`: `string` (encrypted)
- `content`: `string` (encrypted)
- `remindMeBeforeDays`: `number` (encrypted)
- `expirationNotified`: `boolean` (encrypted)
- `archived`: `boolean` (encrypted)

# Occurrence

- `pk`: `USER#userId`
- `sk`: `EVENT#eventId|OCCURRENCE#occurrenceId`
- `date`: `LocalDate` (encrypted)
- `dateBucket`: `YYYY-MM`
- `time`: `LocalTime` (encrypted)
- `status`: `OccurrenceStatus` (encrypted)
- `note`: `string` (encrypted)
- `remindMeBeforeDays`: `number` (encrypted)
- `reminded`: `boolean` (encrypted)

### GSI-pk-date_bucket - Query occurrences of month
- pk: `USER#userId`
- sk: `dateBucket`

# Label

- `pk`: `USER#userId`
- `sk`: `LABEL#labelId`
- `label`: `string` (encrypted)

# Event-Label mapping

Double-mapping for bi-directional query (Query events of label and label of events)

- `pk`: `USER#userId`
- `sk`: `EVENT_LABEL_MAPPING|EVENT#eventId`
- `labelIds`: `uuid[]`

- `pk`: `USER#userId`
- `sk`: `EVENT_LABEL_MAPPING|LABEL#labelId|`
- `eventIds`: `uuid[]`