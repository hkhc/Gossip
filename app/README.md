# Gossip

## Message Format

### General Package Format

- Numbers in small-endian

| Offset | Length (bytes) | Type | Description |
|-|-|-|-|
| 0 | 2 | uint | Message Length |
|  | 1 | byte | Message Type |
|  | 4 | long | Timestamp |
|  | 2 | uint | Message tag. e.g. relating request and response |
|  | n | byte[] | Payload |

### Message Type

| Form Client/Host | Type | Description |
|-|-|-|
| Client | 01 | Join group |
| Client | 02 | Quit group |

### Payload format

## TODO

- [x] Main App Sekeleton
- [ ] Design message package format
- [ ] Specify `android:fullBackupContent` in AndroidManifest.xml
- 