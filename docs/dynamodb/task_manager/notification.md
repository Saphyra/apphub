# `apphub-task_manager-${Environment}-notification`

- `user`: `USER#userId` (recipient)
- `notification`: `NOTIFICATION#notificationId`
- `organizationId`: `ORGANIZATION#organizationId`
- `status`: `enum: NotificationStatus`
- `notificationType`: `enum: NotificationType`
- `createdAt`: `timestamp`
- `lastModified`: `timestamp`
- `expiration`: `timestamp` (ttl)
- `data`: `JSON Map<String, String>`

## GSI-notification-organization-user

- `organization`: `ORGANIZATION#organizationId`
- `user`: `USER#userId` (recipient)