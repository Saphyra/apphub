# `apphub-task_manager-${Environment}-notification`

- `pk`: `USER#userId` (recipient)
- `sk`: `NOTIFICATION#notificationId`
- `organizationId`: `ORGANIZATION#organizationId`
- `status`: `enum: NotificationStatus`
- `notificationType`: `enum: NotificationType`
- `createdAt`: `timestamp`
- `lastModified`: `timestamp`
- `expiration`: `timestamp` (ttl)
- `data`: `JSON Map<String, String>`

## GSI-notification-organization_id-user_id

- `pk`: `ORGANIZATION#organizationId`
- `sk`: `USER#userId` (recipient)