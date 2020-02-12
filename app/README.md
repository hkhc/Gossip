# Gossip

## Usage

- Two instances of the app can communicate over local network.
- The IP of host device is discovered automatically
- Start one app first, enter a name, turn on host mode, then start
- Start another app, enter a name, just start (host mode = off)
- The host mode device will publish the service record with DNS-SD, and other instance of the app can find it
- Type the message at the bottom of screen to send.
- Some canned message at the side menu.
- Messages are totally in memory.
- The host app is actually a host and client at the same time, it connect to localhost to act as yet another client.

## Architecutre

- Use more or less standard AndroidX MVVM architecture. that the app can be divided into multiple layer:
  - Layout/UI + Activity/Fragment
  - ViewModel, which use LiveData to communicate with UI
  - Model, plain object for "business logic", that is how chat messages are handled
- Network communication make use of Netty library based on Java NIO, with underlying TCP connections.

## How to build the app

- Given Android SDK or Android Studio is in place, it should be able to build out of the box. At project directory

  ```
  ./gradlew app:assembleDebug
  ```

  []()

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
| Shutdown | 03 | Shutdown the local daemon |
| Chat | 04 | Send a chat message |

#### 01-JoinGroup

|           | Type |     |
| --------- |- |---- |
| member ID | Short |     |
| member Name length | Short (in number of char) | |
| member Name | CharSequence | |
| lastRead message index | Int | |

#### 02-QuitGroup

|           | Type |     |
| --------- |- |---- |
| member ID | Short |     |

#### 03-Shutdown
No body

#### 04-Chat
|           | Type |     |
| --------- |- |---- |
| text len | Short |     |
| text  | CharSequence |     |




