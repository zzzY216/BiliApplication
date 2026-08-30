# BiliApplication API 接口文档

> 来源：第三方 B 站客户端 [PiliPlus](https://github.com/bggRGjQaUbCoE/PiliPlus)（Flutter 实现）的
> `lib/http/*.dart` 接口定义整理。PiliPlus 是基于 [bilibili-API-collect](https://github.com/SocialSisterYi/bilibili-API-collect)
> 官方社区文档实现的活跃客户端，其接口定义经过实际运行验证，可信度高。
>
> 本文档用途：为 BiliApplication（Android/Compose）接入 B 站接口提供参考清单。
> 本文档只描述"接什么、怎么调"，落地到本项目时的仓储/模型封装见《重构记录-第二轮.md》。

---

## 1. 域名表（Base URL）

| 名称 | URL | 用途 |
| --- | --- | --- |
| api | `https://api.bilibili.com` | **默认**，绝大多数 web 接口 |
| app | `https://app.bilibili.com` | App 端接口（推荐流/点赞/投币/空间等） |
| passport | `https://passport.bilibili.com` | 登录/认证 |
| live | `https://api.live.bilibili.com` | 直播 |
| vc | `https://api.vc.bilibili.com` | 私信/消息 |
| message | `https://message.bilibili.com` | 系统通知 |
| account | `https://account.bilibili.com` | 硬币余额 |
| space | `https://space.bilibili.com` | 空间页/举报 |
| s.search | `https://s.search.bilibili.com` | 热搜/联想词 |

> PiliPlus 源码中 `HttpString`：`apiBaseUrl`（默认）/ `appBaseUrl` / `passBaseUrl` / `liveBaseUrl` / `tUrl` / `messageBaseUrl` / `accountBaseUrl` / `spaceBaseUrl`。

## 2. 通用约定

- **响应结构**（绝大多数接口）：`{"code": 0, "message": "0", "ttl": 1, "data": {...}}`；`code == 0` 为成功。
- **登录态**：web 接口靠 Cookie（`SESSDATA` / `bili_jct` / `DedeUserID`）；app 接口可用 `access_key`。
- **CSRF**：所有写操作（点赞/投币/评论/收藏/关注/发弹幕/删评…）需要 `csrf`（= Cookie 中的 `bili_jct`），
  同时 Cookie 里要有 `SESSDATA`。本项目 `BiliSessionManager` 已维护 Cookie 镜像，写接口时带上 `bili_jct` 即可。
- **WBI 签名**：部分 web 接口需要 `w_rid` + `wts` 签名（见各接口标注 ⚠️WBI），算法见
  [bilibili-API-collect wbi](https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/misc/sign/wbi.md)。
- **App 签名**：app.bilibili.com 与 passport 的 app 接口需要 `appkey` + `sign`（MD5），且需带
  `buvid` 等头。PiliPlus 用 `AppSign.appSign()`。**本项目当前接入的 app 接口较少，签名是最大门槛，建议优先 web 接口。**
- **UA**：部分接口对 `User-Agent` 敏感，建议统一维护一个 UA 常量（PiliPlus 有 `browser_ua.dart`）。
- **返回字段**：各接口 `data` 结构以 [bilibili-API-collect](https://github.com/SocialSisterYi/bilibili-API-collect) 对应页面为准，本文档标注关键字段。

---

## 3. 登录认证（passport.bilibili.com）

| 接口 | 方法 | URL | 鉴权 | 说明 |
| --- | --- | --- | --- | --- |
| 申请二维码（TV 端） | POST | `/x/passport-tv-login/qrcode/auth_code` | 公开 | 参数 `local_id`(设备ID)、`platform=android`、`mobi_app=android_hd`(+appSign)；返回 `auth_code`、`url`（二维码内容） |
| 扫码状态轮询 | POST | `/x/passport-tv-login/qrcode/poll` | 公开 | 参数 `auth_code`、`local_id`(+appSign)；code=0 成功、86038 过期、86090 已扫待确认 |
| Cookie 换 access_key | POST | `/x/passport-tv-login/h5/qrcode/confirm` | 登录 Cookie | 扫码成功后用 Cookie 换取 `access_key`（app 接口登录态） |
| 密码加密公钥 | GET | `/x/passport-login/web/key` | 公开 | 返回 RSA 公钥 + salt，用于密码登录 |
| Web 密码登录 | POST | `/x/passport-login/web/login` | 公开 | 表单含 RSA 加密密码、`csrf`、`source=main_web` |
| App 密码登录 | POST | `/x/passport-login/oauth2/login` | 公开 | 参数极多（`username`/`password`(RSA 加密)/`device`/`buvid`/`statistics`…+appSign）；返回 access_token/cookies |
| App 短信验证码 | POST | `/x/passport-login/sms/send` | 公开 | `cid`、`tel` + appSign |
| App 短信登录 | POST | `/x/passport-login/login/sms` | 公开 | `captcha_key`、`tel`、`code`、`cid`、`key` + appSign |
| OAuth 换 token | POST | `/x/passport-login/oauth2/access_token` | 公开 | `code`（oauthCode）+ appSign → `access_token` |
| 登出 | POST | `/login/exit/v2` | 登录 Cookie | `biliCSRF` |
| 登录设备列表 | GET | `/x/safecenter/user_login_devices` | 登录态 | `access_key`/`csrf` + appSign |
| 风控手机信息 | GET | `/x/safecenter/user/info` | 登录 Cookie | 参数 `tmp_code`（密码登录风控） |
| 风控验证码预取 | POST | `/x/safecenter/captcha/pre` | 公开 | 极验预取 |
| 风控发短信 | POST | `/x/safecenter/common/sms/send` | 公开 | `sms_type=loginTelCheck`、`tmp_code` + appSign |
| 风控提交验证码 | POST | `/x/safecenter/login/tel/verify` | 公开 | `code`、`tmp_code`、`request_id`、`captcha_key` + appSign |

> **与本项目现状的关系**：BiliApplication 已实现「申请二维码 + 轮询 + 存 Cookie」，正是上表第 1、2、3 行。
> 密码/短信登录需要 RSA 加密与 appSign，工程量较大，本期建议只保留扫码登录。

---

## 4. 视频

### 4.1 信息流

| 接口 | 方法 | URL | 鉴权 | 关键参数 | 说明 |
| --- | --- | --- | --- | --- | --- |
| 推荐流（Web）⚠️WBI | GET | `/x/web-interface/wbi/index/top/feed/rcmd` | 公开/登录态 | `version=1`、`feed_version=V8`、`homepage_ver=1`、`ps`、`fresh_idx`、`brush`、`fresh_type=4` | `data.item[]`，过滤 `goto=='av'` 的普通视频；刷新用新 `fresh_idx` |
| 推荐流（App） | GET | `https://app.bilibili.com/x/v2/feed/index` | 公开/登录态 | `idx`、`fnval`、`fnver`、`qn`、`pull`、`flush`、`device`、`mobi_app=android_hd` 等 + **buvid 头** | `data.items[]`，`card_goto` 区分视频/广告；需 appSign + 设备头，门槛高 |
| 热门视频 | GET | `/x/web-interface/popular` | 公开 | `pn`、`ps` | `data.list[]`（含 `owner/stat/title/desc` 等） |
| 热门-特殊榜单列表 | GET | `/x/web-interface/popular/series/list` | 公开 | `web_location` ⚠️WBI | `data.list[]`（系列榜单 id） |
| 热门-某榜单 | GET | `/x/web-interface/popular/series/one` | 公开 | `number` ⚠️WBI | 榜单内视频列表 |
| 热门-入站必刷 | GET | `/x/web-interface/popular/precious` | 公开 | `page`、`page_size` ⚠️WBI | `data.list[]` |
| 排行榜 | GET | `/x/web-interface/ranking/v2` | 公开 | `rid`（分区 id）、`type=all` ⚠️WBI | `data.list[]`；`rid=0` 全站 |

> **本项目现状**：首页已用 App 端推荐（`app.bilibili.com/x/v2/feed/index`）；发现页占位待接，可直接用「热门视频」或「排行榜」。

### 4.2 播放

| 接口 | 方法 | URL | 鉴权 | 关键参数 | 说明 |
| --- | --- | --- | --- | --- | --- |
| 播放地址（UGC）⚠️WBI | GET | `/x/player/wbi/playurl` | 公开（高画质需登录） | `bvid` 或 `aid`、`cid`、`qn`、`fnval`(多格式)、`fnver`、`fourk=1`、`voice_balance`、`try_look=1`(免登 1080p) | `data.durl[]` / `dash`（视频/音频分片）；PiliPlus 用 `fnval=4048` |
| 播放地址（PGC） | GET | `/pgc/player/web/v2/playurl` | 公开/登录态 | `ep_id`、`cid`、`qn`、`fnval` | `result.video_info`（含 lastPlayTime） |
| 播放地址（课程 PUGV） | GET | `/pugv/player/web/playurl` | 登录态 | `ep_id`、`cid` | `data` |
| 播放地址（TV） | GET | `/x/tv/playurl` | 登录态 | `cid`、`object_id`、`playurl_type`、`qn`、`access_key` + appSign | TV 端 |
| 分 P 列表 | GET | `/x/player/pagelist` | 公开 | `bvid` 或 `aid` | 返回分 P 及其 `cid`（bvid→cid 常用） |
| 播放信息（字幕/音轨）⚠️WBI | GET | `/x/player/wbi/v2` | 公开/登录态 | `bvid`/`aid`、`cid` | `data.subtitle.subtitles[]` |
| 视频截图预览 | GET | `/x/player/videoshot` | 公开 | `bvid`、`cid`、`index=1` | 封面帧序列图 |
| 在线观看人数 | GET | `/x/player/online/total` | 公开 | `aid`、`bvid`、`cid` | `data.total` |

### 4.3 详情与互动

| 接口 | 方法 | URL | 鉴权 | 关键参数 | 说明 |
| --- | --- | --- | --- | --- | --- |
| 视频详情 | GET | `/x/web-interface/view` | 公开 | `bvid` 或 `aid` | 标题/简介/分区/UP/`stat`（view/danmaku/reply/fav/share/like）/`cid` |
| 点赞/投币/收藏状态 | GET | `/x/web-interface/archive/relation` | 登录 Cookie | `aid`、`bvid` | `data.like/coin/favorite`（0/1） |
| 相关视频 | GET | `/x/web-interface/archive/related` | 公开 | `bvid` | 详情页"相关推荐"列表 |
| 视频标签 | GET | `/x/web-interface/view/detail/tag` | 公开 | `bvid` | 标签列表 |
| 点赞/取消赞 | POST | `https://app.bilibili.com/x/v2/view/like` | 登录 Cookie | `aid`、`like`（1 赞 / 0 取消） | App 端点赞 |
| 点踩 | POST | `https://app.bilibili.com/x/v2/view/dislike` | 登录 Cookie | `aid`、`dislike`（1 踩 / 0 取消） | 需 access_key |
| 投币 | POST | `https://app.bilibili.com/x/v2/view/coin/add` | 登录 Cookie | `aid`、`multiply`(1-2)、`select_like`(0/1) | App 端投币 |
| 一键三连 | POST | `/x/web-interface/archive/like/triple` | 登录 Cookie | `aid`、`csrf`、`eab_x`、`ga`、`source=web_normal` | 赞+币+藏 |
| AI 总结 ⚠️WBI | GET | `/x/web-interface/view/conclusion/get` | 公开 | `bvid`、`cid`、`up_mid` | 视频 AI 摘要（部分视频可用） |

> **本项目现状**：`VideoRepository` 已有 播放地址/详情/评论/点赞/是否点赞；上表可补充「收藏状态」「投币」「三连」。

---

## 5. 评论

| 接口 | 方法 | URL | 鉴权 | 关键参数 | 说明 |
| --- | --- | --- | --- | --- | --- |
| 评论列表 | GET | `/x/v2/reply/main`（未登录）/ `/x/v2/reply`（登录） | 公开/登录态 | 未登录：`oid`、`type`、`pagination_str`(JSON `{"offset":"..."}`)、`mode`(2 时间/3 热度)；登录：`oid`、`type`、`sort`(1 热度/2 时间)、`pn`、`ps=20` | `data.replies[]` + `data.cursor`（游标分页，`next_offset` 用于下次请求） |
| 楼中楼 | GET | `/x/v2/reply/reply` | 公开/登录态 | `oid`、`root`(根评论 rpid)、`pn`、`type`、`sort`、登录时带 `csrf` | 二级评论列表 |
| 评论点赞 | POST | `/x/v2/reply/action` | 登录 Cookie | `type`、`oid`、`rpid`、`action`(1 赞/0 取消)、`csrf` | |
| 评论点踩 | POST | `/x/v2/reply/hate` | 登录 Cookie | 同上 `action`(1 踩/0 取消) | |
| 发表评论 | POST | `/x/v2/reply/add` | 登录 Cookie | `type`、`oid`、`message`(≤1000字)、`root`、`parent`、`plat=2`(安卓)、`csrf`；可带 `at_name_to_mid`/`pictures` | 支持@/图片 |
| 删除评论 | POST | `/x/v2/reply/del` | 登录 Cookie | `type`、`oid`、`rpid`、`csrf` | |
| 置顶评论 | GET | `/x/v2/reply/top` | 公开 | `type`、`oid` | 置顶/热评 |
| 评论举报 | POST | `/x/v2/reply/report` | 登录 Cookie | `type`、`oid`、`rpid`、`reason`、`csrf` | |
| 评论区互动状态 | GET | `/x/v2/reply/subject/interaction-status` | 登录 Cookie | `type`、`oid` | 是否关闭评论等 |

---

## 6. 用户 / 我的

| 接口 | 方法 | URL | 鉴权 | 关键参数 | 说明 |
| --- | --- | --- | --- | --- | --- |
| 我的信息 | GET | `/x/web-interface/nav` | 登录 Cookie | 无 | 昵称/头像/mid/硬币 `money`/`wbi_img`(签名密钥) |
| 我的状态 | GET | `/x/web-interface/nav/stat` | 登录 Cookie | 无 | 收藏/关注/粉丝数等 |
| 用户关系统计 | GET | `/x/relation/stat` | 公开 | `vmid` | 关注数/粉丝数 |
| 用户名片 | GET | `/x/web-interface/card` | 公开 | `mid` | 头像/昵称/签名 |
| 用户空间信息 ⚠️WBI | GET | `/x/space/wbi/acc/info` | 公开 | `mid` | 主页详情（等级/勋章） |
| 用户 UP 数据 | GET | `/x/space/upstat` | 公开 | `mid` | 获赞/播放/阅读数 |
| 用户投稿列表 ⚠️WBI | GET | `/x/space/wbi/arc/search` | 公开 | `mid`、`ps`、`pn`、`tid`、`order`(pubdate/view)、`keyword`、`platform=web` | 分页 |
| 置顶视频 | GET | `/x/space/top/arc` | 公开 | `mid` | 置顶投稿 |
| 最近投币视频 | GET | `/x/space/coin/video` | 登录 Cookie | `vmid`、`gaia_source=main_web` ⚠️WBI | |
| 最近点赞视频 | GET | `/x/space/like/video` | 登录 Cookie | `vmid` ⚠️WBI | |
| 追番列表 | GET | `/x/space/bangumi/follow/list` | 登录 Cookie | `type`(1 番剧/2 影视)、`pn`、`ps` | 我的追番 |
| 稍后再看 | GET | `/x/v2/history/toview/web` | 登录 Cookie | `pn`、`ps`、`viewed`、`key`、`asc` ⚠️WBI | 列表 |
| 添加稍后再看 | POST | `/x/v2/history/toview/add` | 登录 Cookie | `aid` 或 `bvid`、`csrf` | |
| 移除稍后再看 | POST | `/x/v2/history/toview/v2/dels` | 登录 Cookie | `resources`(aid 逗号分隔)、`csrf` | |
| 清空稍后再看 | POST | `/x/v2/history/toview/clear` | 登录 Cookie | `csrf`、`clean_type` | |
| 播放进度上报 | POST | `/x/click-interface/web/heartbeat` | 登录 Cookie | `bvid`/`aid`、`cid`、`played_time`、`type`、`csrf` | 记进度（建议播放器接入） |

---

## 7. 关注 / 粉丝 / 黑名单

| 接口 | 方法 | URL | 鉴权 | 关键参数 | 说明 |
| --- | --- | --- | --- | --- | --- |
| 查询关系 | GET | `/x/relation` | 登录 Cookie | `fid` | 与某人关系（关注/特别关注） |
| 批量查询关系 | GET | `/x/relation/relations` | 登录 Cookie | `fids` | 批量 |
| 操作关系 | POST | `/x/relation/modify` | 登录 Cookie | `fid`、`act`(1 关注/2 取关/5 拉黑/6 移除)、`re_src`、`csrf` | 关注/取关/黑名单 |
| 关注列表 | GET | `/x/relation/followings` | 登录 Cookie | `vmid`、`pn`、`ps`、`order`、`order_type` | 分页 |
| 粉丝列表 | GET | `/x/relation/fans` | 登录 Cookie | `vmid`、`pn`、`ps` | 分页 |
| 搜索关注 | GET | `/x/relation/followings/search` | 登录 Cookie | `vmid`、`keyword`、`pn` | |
| 共同关注 | GET | `/x/relation/same/followings` | 登录 Cookie | `vmid` | |
| 已关注 UP 更新 | GET | `/x/relation/followings/followed_upper` | 登录 Cookie | `pn`、`vmid` | 关注 UP 的新投稿 |
| 黑名单 | GET | `/x/relation/blacks` | 登录 Cookie | `pn`、`ps`、`re_version=0`、`jsonp=jsonp`、`csrf` | 列表 |
| 关注分组 | GET | `/x/relation/tags` | 登录 Cookie | 无 | 分组列表 |
| 分组排序 | POST | `/x/relation/tags/update_sort` | 登录 Cookie | `tagids`(逗号分隔)、`csrf` | |
| 分组详情 | GET | `/x/relation/tag` | 登录 Cookie | `tagid` | 组内 UP |
| 创建/改/删分组 | POST | `/x/relation/tag/create` `/update` `/del` | 登录 Cookie | `tag`、`tagid`、`csrf` | |
| 分组增删成员 | POST | `/x/relation/tags/addUsers` | 登录 Cookie | `fids`、`tagids`、`csrf` | |
| 特别关注 | POST | `/x/relation/tag/special/add` `/del` | 登录 Cookie | `fid`、`tagid`、`csrf` | |

---

## 8. 收藏

| 接口 | 方法 | URL | 鉴权 | 关键参数 | 说明 |
| --- | --- | --- | --- | --- | --- |
| 收藏夹内容 | GET | `/x/v3/fav/resource/list` | 登录 Cookie | `media_id`、`pn`、`ps`、`keyword`、`order`(mtime/view/pubtime)、`tid`、`platform=web`、`type`(0 当前/1 全部) | `data.medias[]` + `cursor` |
| 收藏/取消收藏 | POST | `/x/v3/fav/resource/batch-deal` | 登录 Cookie | `resources`(avid/bvid)、`add_media_ids`、`del_media_ids`、`csrf` | 批量加入/移出收藏夹（空串表示不增/不减） |
| 取消全部收藏 | POST | `/x/v3/fav/resource/unfav-all` | 登录 Cookie | `rid`、`type`、`csrf` | |
| 复制/移动收藏 | POST | `/x/v3/fav/resource/copy` `/move` | 登录 Cookie | `src_media_id`、`tar_media_id`、`resources`、`csrf` | |
| 清空失效收藏 | POST | `/x/v3/fav/resource/clean` | 登录 Cookie | `media_id`、`csrf` | |
| 收藏夹排序 | POST | `/x/v3/fav/resource/sort` | 登录 Cookie | `media_id`、`ids`、`csrf` | |
| 我的收藏夹列表 | GET | `/x/v3/fav/folder/created/list` | 登录 Cookie | `up_mid`、`pn`、`ps` | 我创建的 |
| 全部收藏夹(含是否已收藏) | GET | `/x/v3/fav/folder/created/list-all` | 登录 Cookie | `up_mid`、`type`(0 全部/2 视频)、`rid` | 判断视频在哪些收藏夹 |
| 收藏夹详情 | GET | `/x/v3/fav/folder/info` | 公开 | `media_id` | 名称/数量/封面 |
| 创建/改/删收藏夹 | POST | `/x/v3/fav/folder/add` `/edit` `/del` | 登录 Cookie | `title`、`media_id`、`privacy`、`csrf` | |
| 收藏夹排序 | POST | `/x/v3/fav/folder/sort` | 登录 Cookie | `ids`、`csrf` | |
| 关注收藏夹 | POST | `/x/v3/fav/folder/fav` `/unfav` | 登录 Cookie | `media_id`、`csrf` | |
| 订阅收藏夹列表 | GET | `/x/v3/fav/folder/collected/list` | 登录 Cookie | `up_mid`、`pn`、`ps` | 我订阅的 |
| 稍后再看/收藏夹视频 | GET | `/x/v2/medialist/resource/list` | 登录 Cookie | `type`、`biz_id`、`oid`、`ps`、`desc`、`sort_field` | 通用列表 |

> **本项目现状**：底部 Tab「收藏」为占位页，可直接按上表接入「我的收藏夹列表 → 收藏夹内容」。

---

## 9. 搜索

| 接口 | 方法 | URL | 鉴权 | 关键参数 | 说明 |
| --- | --- | --- | --- | --- | --- |
| 热搜榜 | GET | `https://s.search.bilibili.com/main/hotword` | 公开 | 无 | `data.list[]`（词/热度） |
| 搜索联想词 | GET | `https://s.search.bilibili.com/main/suggest` | 公开 | `term`(输入前缀)、`main_ver`、`highlight` | `data.tag[]` |
| 默认搜索词 | GET | `/x/web-interface/wbi/search/default` | 公开 ⚠️WBI | 无 | `data.show_name` |
| 综合搜索 | GET | `/x/web-interface/wbi/search/all/v2` | 公开 ⚠️WBI | `keyword`、`page`、`order`(totalclick/click/pubdate)、`duration`、`tids` | `data.result[]`（多类型聚合） |
| 分类搜索 | GET | `/x/web-interface/wbi/search/type` | 公开 ⚠️WBI | `keyword`、`search_type`(video/bili_user/live_room…)、`page`、`order`、`platform=web` | 单类型 |
| 搜索趋势榜 | GET | `/x/v2/search/trending/ranking` | 公开 | 无 | 搜索榜单 |
| 搜索推荐 | GET | `https://app.bilibili.com/x/v2/search/recommend` | 公开 | 无 | 进入搜索页的推荐词（app） |

---

## 10. 动态

| 接口 | 方法 | URL | 鉴权 | 关键参数 | 说明 |
| --- | --- | --- | --- | --- | --- |
| 首页 UP 状态 | GET | `/x/polymer/web-dynamic/v1/portal` | 登录 Cookie | 无 | 正在直播的 UP、UP 分组 |
| UP 分组列表 | GET | `/x/polymer/web-dynamic/v1/uplist` | 登录 Cookie | `page`、`page_size` | |
| 关注动态流 | GET | `/x/polymer/web-dynamic/v1/feed/all` | 登录 Cookie | `timezone_offset`、`type`、`page`、`offset`(游标)、`features=itemOpusStyle` | `data.items[]`，游标分页 |
| 动态详情 | GET | `/x/polymer/web-dynamic/v1/detail` | 登录 Cookie | `id`、`timezone_offset`、`features` | 单条动态 |
| 用户动态 | GET | `/x/polymer/web-dynamic/v1/feed/space` | 公开 | `host_mid`、`offset`、`page`、`features` | 空间页动态 |
| 动态点赞/取消 | POST | `/x/dynamic/feed/dyn/thumb` | 登录 Cookie | `dyn_id`、`up`(1 赞/2 取消)、`csrf` | |
| 未读动态数 | GET | `/x/web-interface/dynamic/entrance` | 登录 Cookie | 无 | 红点 |
| 发文字动态 | POST | `/dynamic_svr/v1/dynamic_svr/create` | 登录 Cookie | `dynamic_id`(0)、`content`、`up_choose_channel`、`csrf` | |
| 发图文动态 | POST | `/x/dynamic/feed/create/dyn` | 登录 Cookie | `biz`、`content`、`pictures`、`csrf` | |
| 删动态 | POST | `/x/dynamic/feed/operate/remove` | 登录 Cookie | `dyn_id`、`csrf` | |
| 动态举报 | POST | `/x/dynamic/feed/dynamic_report/add` | 登录 Cookie | `accused_uid`、`dynamic_id`、`reason_type`、`csrf` | |
| 主题动态流 | GET | `/x/polymer/web-dynamic/v1/feed/topic` | 公开 | `topic_id`、`offset`、`page` | |
| 主题详情 | GET | `/x/topic/web/details/top` | 公开 | `topic_id` | |

---

## 11. 番剧 / 影视（PGC）

| 接口 | 方法 | URL | 鉴权 | 关键参数 | 说明 |
| --- | --- | --- | --- | --- | --- |
| 剧集明细 | GET | `/pgc/view/web/season` | 公开/登录态 | `season_id` | 番剧/影视信息+分集 |
| 单集信息 | GET | `/pgc/season/episode/web/info` | 公开 | `ep_id` | |
| 追番状态 | GET | `/pgc/view/web/season/user/status` | 登录 Cookie | `season_id` | 是否追/追番进度 |
| 追番/取消追番 | POST | `/pgc/web/follow/add` `/del` | 登录 Cookie | `season_id`、`csrf` | |
| 点赞投币收藏状态 | GET | `/pgc/season/episode/community` | 登录 Cookie | `ep_id` | PGC 互动状态 |
| 一键三连(PGC) | POST | `/pgc/season/episode/like/triple` | 登录 Cookie | `ep_id`、`csrf` | |
| PGC 排行榜 | GET | `/pgc/web/rank/list` | 公开 | `day`、`season_type` ⚠️WBI | |
| PGC 季度榜 | GET | `/pgc/season/rank/web/list` | 公开 | `day`、`season_type` ⚠️WBI | |
| 索引筛选条件 | GET | `/pgc/season/index/condition` | 公开 | 无 | 分区/年份/类型筛选项 |
| 索引结果 | GET | `/pgc/season/index/result` | 公开 | `season_type`、`pn`、`ps`、`order`、`type`、`year` | 按条件筛选 |
| 番剧时间线 | GET | `/pgc/web/timeline` | 公开 | `season_type` | 新番时间表 |
| 长评/短评列表 | GET | `/pgc/review/long/list` `/short/list` | 公开/登录态 | `season_id`、`pn`、`ps` | |
| 短评发布 | POST | `/pgc/review/short/post` | 登录 Cookie | `media_id`、`content`、`score`、`share_feed`、`csrf` | |
| 修改短评 | POST | `/pgc/review/short/modify` | 登录 Cookie | `media_id`、`review_id`、`content`、`score`、`csrf` | |
| 删除短评 | POST | `/pgc/review/short/del` | 登录 Cookie | `media_id`、`review_id`、`csrf` | |
| 评价点赞/点踩 | POST | `/pgc/review/action/like` `/dislike` | 登录 Cookie | `media_id`、`review_id`、`review_type=2`、`csrf` | |

---

## 12. 直播（api.live.bilibili.com）

| 接口 | 方法 | URL | 鉴权 | 关键参数 | 说明 |
| --- | --- | --- | --- | --- | --- |
| 直播推荐(Web) | GET | `/xlive/web-interface/v1/second/getUserRecommend` | 公开 | `page`、`page_size`、`platform=web` | 列表 |
| 直播推荐(App) | GET | `/xlive/app-interface/v2/index/feed` | 公开 | `page`、`page_size` | |
| 二级分类直播 | GET | `/xlive/app-interface/v2/second/getList` | 公开 | `parent_area_id`、`page`、`page_size` | |
| 直播间信息 | GET | `/xlive/web-room/v2/index/getRoomPlayInfo` | 公开 | `room_id`、`qn`(80/150/400/10000/20000/30000)、`protocol`、`format`、`codec` | 播放流地址 |
| 直播间 H5 信息 | GET | `/xlive/web-room/v1/index/getH5InfoByRoom` | 公开 | `room_id` | |
| 弹幕历史 | GET | `/xlive/web-room/v1/dM/gethistory` | 公开 | `roomid` | |
| 弹幕密钥 | GET | `/xlive/web-room/v1/index/getDanmuInfo` | 公开 | `id`(room_id) | 弹幕 ws 连接 token |
| 发直播弹幕 | POST | `/msg/send` | 登录 Cookie + ⚠️WBI | `roomid`、`msg`、`csrf`/`csrf_token`、`rnd`(秒级时间戳)、`web_location=444.8`(签名)、`bubble`、`color`、`mode`、`fontsize`、`dm_type` | 表情弹幕走 `emoticonOptions` 分支 |
| 直播表情包 | GET | `/xlive/web-ucenter/v2/emoticon/GetEmoticons` | 公开 | `platform=pc`、`room_id` | 直播间表情列表 |
| 粉丝勋章墙 | GET | `/xlive/web-ucenter/user/MedalWall` | 登录 Cookie | `target_id`(用户 mid) | |
| 直播贡献榜 | GET | `/xlive/general-interface/v1/rank/queryContributionRank` | 公开 ⚠️WBI | `ruid`、`room_id`、`page`、`page_size`、`type`(online_rank/daily_rank/weekly_rank/monthly_rank)、`switch`、`platform=web` | |
| 进房上报 | POST | `/xlive/web-room/v1/index/roomEntryAction` | 登录 Cookie | `room_id`、`platform=pc`、`csrf` | |
| 关注直播用户 | GET | `/xlive/web-ucenter/user/following` | 登录 Cookie | `pn`、`ps` | |
| 直播搜索 | GET | `/xlive/app-interface/v2/search_live` | 公开 | `keyword`、`page`、`page_size` | |
| 分区列表 | GET | `/xlive/app-interface/v2/index/getAreaList` / `/room/v1/Area/getList` | 公开 | 无 | |
| 用户直播间状态 | GET | `/xlive/web-room/v1/index/getInfoByUser` | 公开 | `room_id` | |
| SC 列表 | GET | `/av/v1/SuperChat/getMessageList` | 公开 | `room_id`、`page` | |
| 大航海列表 | GET | `/xlive/app-ucenter/v1/guard/MainGuardCardAll` | 公开 | `room_id`、`page`、`page_size` | |

---

## 13. 弹幕

| 接口 | 方法 | URL | 鉴权 | 关键参数 | 说明 |
| --- | --- | --- | --- | --- | --- |
| 发送弹幕 | POST | `/x/v2/dm/post` | 登录 Cookie | `type=1`、`oid`(cid)、`msg`(≤100字)、`bvid`、`mode`(1 滚动/4 底端/5 顶端)、`progress`(ms)、`color`、`fontsize`、`pool`、`rnd`(时间戳降频)、`colorful`(大会员彩弹)、`checkbox_type`、`csrf` | `data` 返回 `dmid` |
| 弹幕点赞/取消 | POST | `/x/v2/dm/thumbup/add` | 登录 Cookie | `op`(1 赞/2 取消)、`dmid`、`oid`(cid)、`platform=web_player`、`polaris_app_id=100`、`polaris_platform=5`、`spmid`、`statistics`、`csrf` | |
| 弹幕举报 | POST | `/x/dm/report/add` | 登录 Cookie | `cid`、`dmid`、`reason`、`block`(是否同时屏蔽)、`originCid`、`content`、`polaris_app_id`、`polaris_platform`、`spmid`、`statistics`、`csrf` | |
| 撤回弹幕 | POST | `/x/dm/recall` | 登录 Cookie | `dmid`、`cid`、`type=1`、`csrf` | |
| 批量删除/保护弹幕 | POST | `/x/dm/recall` | 登录 Cookie | `dmids`(逗号分隔)、`oid`(cid)、`state`(0 取消删除/1 删除/2 保护/3 取消保护)、`type=1`、`csrf` | 弹幕管理 |
| 屏蔽词列表 | GET | `/x/dm/filter/user` | 登录 Cookie | 无 | |
| 添加屏蔽词 | POST | `/x/dm/filter/user/add` | 登录 Cookie | `type`(0 关键词/1 正则/2 用户)、`filter`、`csrf` | |
| 删除屏蔽词 | POST | `/x/dm/filter/user/del` | 登录 Cookie | `ids`、`csrf` | |
| 弹幕编辑状态 | POST | `/x/v2/dm/edit/state` | 登录 Cookie | `dmid_str`、`state`、`csrf` | |

> 历史弹幕走 `/x/v2/dm/web/seg.so`（二进制）或 gRPC，本期不接入。

---

## 14. 消息 / 私信

| 接口 | 方法 | URL | 鉴权 | 关键参数 | 说明 |
| --- | --- | --- | --- | --- | --- |
| 未读私信数 | GET | `https://api.vc.bilibili.com/session_svr/v1/session_svr/single_unread` | 登录 Cookie | `build=0`、`mobi_app=web`、`unread_type=0` | |
| 未读消息中心 | GET | `/x/msgfeed/unread` | 登录 Cookie | `build=0`、`mobi_app=web` | 回复/@/赞/系统 未读数 |
| 回复消息 | GET | `/x/msgfeed/reply` | 登录 Cookie | `id`/`reply_time`(游标)、`platform=web`、`build=0`、`mobi_app=web` | 游标分页 |
| @消息 | GET | `/x/msgfeed/at` | 登录 Cookie | `id`/`at_time`(游标)、`platform=web`、`build=0`、`mobi_app=web` | |
| 赞消息 | GET | `/x/msgfeed/like` | 登录 Cookie | `id`/`like_time`(游标)、`platform=web`、`build=0`、`mobi_app=web` | |
| 点赞详情列表 | GET | `/x/msgfeed/like_detail` | 登录 Cookie | `card_id`、`pn`、`last_mid`、`platform=web`、`build=0` | 谁赞了我 |
| 系统通知 | GET | `https://message.bilibili.com/x/sys-msg/query_notify_list` | 登录 Cookie | `cursor`、`page_size` | |
| 系统通知已读 | GET | `https://message.bilibili.com/x/sys-msg/update_cursor` | 登录 Cookie | `cursor`、`csrf` | |
| 删除通知 | POST | `/x/msgfeed/del` | 登录 Cookie | `tp`、`id`、`build=0`、`mobi_app=web`、`csrf` | |
| 删除系统通知 | POST | `https://message.bilibili.com/x/sys-msg/del_notify_list` | 登录 Cookie | `ids`(数组)、`station_ids`、`type=4`、`csrf` | |
| 通知开关 | POST | `/x/msgfeed/notice` | 登录 Cookie | `tp`、`id`、`notice_state`、`build=0`、`mobi_app=web`、`csrf` | |
| 上传图片 | POST | `/x/upload/web/image` | 登录 Cookie | multipart：`bucket`、`file`、`dir`、`csrf` | 通用图片上传 |
| 上传动态图片 | POST | `/x/dynamic/feed/draw/upload_bfs` | 登录 Cookie | multipart：`file_up`、`category`、`biz`、`csrf` | BFS 动态图片 |
| 会话列表 | GET | `https://api.vc.bilibili.com/session_svr/v1/session_svr/get_sessions` | 登录 Cookie | `session_type`、`group_fold`、`unfollow_fold`、`sort_rule` ⚠️WBI | 私信会话 |
| 私信用户信息 | GET | `https://api.vc.bilibili.com/account/v1/user/cards` | 登录 Cookie | `uids` | 批量 |
| 会话消息 | GET | `https://api.vc.bilibili.com/svr_sync/v1/svr_sync/fetch_session_msgs` | 登录 Cookie | `talker_id`、`session_type`、`size` ⚠️WBI | 历史消息 |
| 标记已读 | GET | `https://api.vc.bilibili.com/session_svr/v1/session_svr/update_ack` | 登录 Cookie | `talker_id`、`session_type`、`ack_seqno`、`build=0`、`mobi_app=web`、`csrf` ⚠️WBI | |
| 删除会话 | POST | `https://api.vc.bilibili.com/session_svr/v1/session_svr/remove_session` | 登录 Cookie | `talker_id`、`session_type=1`、`csrf` ⚠️WBI | |
| 置顶会话 | POST | `https://api.vc.bilibili.com/session_svr/v1/session_svr/set_top` | 登录 Cookie | `talker_id`、`session_type=1`、`op_type`、`csrf` ⚠️WBI | |
| 发送私信 | POST | `https://api.vc.bilibili.com/web_im/v1/web_im/send_msg` | 登录 Cookie | `msg[sender_uid]`、`msg[receiver_id]`、`msg[msg_type]`、`msg[content]`、`csrf` | |
| IM 用户信息 | GET | `https://api.vc.bilibili.com/x/im/user_infos` | 登录 Cookie | `uids`、`build=0`、`mobi_app=web`、`csrf` | |
| 免打扰设置 | POST | `https://api.vc.bilibili.com/link_setting/v1/link_setting/set_msg_dnd` | 登录 Cookie | `uid`、`setting`、`dnd_uid`、`csrf` | |
| 会话推送开关 | POST | `https://api.vc.bilibili.com/link_setting/v1/link_setting/set_push_ss` | 登录 Cookie | `setting`、`talker_uid`、`csrf` | |
| 会话加密信息 | GET | `https://api.vc.bilibili.com/link_setting/v1/link_setting/get_session_ss` | 登录 Cookie | `talker_uid`、`csrf` | 解密私信消息 |
| 举报私信 | POST | `https://api.vc.bilibili.com/x/bplus/im/report/add` | 登录 Cookie | `accused_uid`、`object_id`、`reason_type`、`reason_desc`、`module=604`、`csrf` | |

---

## 15. 其他（排行榜/笔记/风控/杂项）

| 接口 | 方法 | URL | 鉴权 | 关键参数 | 说明 |
| --- | --- | --- | --- | --- | --- |
| 硬币余额 | GET | `https://account.bilibili.com/site/getCoin` | 登录 Cookie | 无 | `data.money` |
| 硬币记录 | GET | `/x/member/web/coin/log` | 登录 Cookie | `jsonp` | |
| 笔记列表(视频) | GET | `/x/note/publish/list/archive` | 登录 Cookie | `oid`、`oid_type`、`pn`、`ps` | 视频速记 |
| 笔记列表(用户) | GET | `/x/note/publish/list/user` | 登录 Cookie | `mid`、`pn`、`ps` | |
| 发布笔记 | POST | `/x/note/add` | 登录 Cookie | `oid`、`oid_type`、`title`、`content`、`csrf` | |
| 删除笔记 | POST | `/x/note/del` | 登录 Cookie | `note_id`、`csrf` | |
| 风控注册 | POST | `/x/gaia-vgate/v1/register` | 登录 Cookie(可选) | `v_voucher`(风控凭证) + `csrf`(登录时) | 风控人机验证 |
| 风控校验 | POST | `/x/gaia-vgate/v1/validate` | 登录 Cookie(可选) | `challenge`、`seccode`、`token`、`validate` + `csrf`(登录时) | 返回 `grisk_id` |
| 赛事信息 | GET | `/x/esports/match/info` | 公开 | `cid`(比赛 id)、`platform=2` | 电竞比赛详情 |
| 投票信息 | GET | `/x/vote/vote_info` | 公开 | `vote_id` | |
| 投票 | POST | `/x/vote/do_vote` | 登录 Cookie | `vote_id`、`votes`、`csrf` | |
| 动态粉丝群 | GET | `/x/web-interface/dynamic/entrance` | 登录 Cookie | 无 | |

---

## 16. 与本项目模块的映射建议

| BiliApplication 模块/页面 | 推荐接入接口（本文档章节） |
| --- | --- |
| `features:login`（扫码登录，已有） | §3 申请二维码 / 轮询 / Cookie 换 access_key |
| `features:home`（首页推荐流，已有） | §4.1 推荐流（App/Web） |
| `features:discover`（发现页，占位） | §4.1 热门视频 / 排行榜 / 入站必刷 |
| `features:library`（收藏页，占位） | §8 收藏夹列表 → 收藏夹内容 |
| `features:profile`（我的页，占位） | §6 我的信息 / 状态 / 稍后再看 / 追番 |
| `core:data VideoRepository`（已有） | §4.2 播放地址、§4.3 详情/点赞、§5 评论 |
| 待接入：搜索 | §9 |
| 待接入：播放器（media3 已引入） | §4.2 播放地址 + §4.3 进度上报 + §13 弹幕 |

## 17. 接入注意事项

1. **优先 web 接口**：绝大多数 web 接口只需 Cookie（SESSDATA/bili_jct），无 appSign 门槛；app.bilibili.com 的接口需要 app 签名，复杂度高。
2. **WBI 签名必做**：带 ⚠️WBI 的接口签名缺失会返回 `-403`。签名需要 `/x/web-interface/nav` 返回的 `wbi_img` 密钥（可用匿名请求获取）。
3. **Cookie 管理**：`BiliSessionManager` 已维护 `SESSDATA/bili_jct/DedeUserID` 镜像；写操作统一补 `csrf=bili_jct`。
4. **错误码**：`-403` 风控/签名、`-404` 资源不存在、`-412` 请求被拦截、`-101` 未登录、`-111` csrf 校验失败、`62002` 稿件不可见。
5. **接口以官方社区文档为准**：单接口细节（如评论 `pagination_str` 构造、推荐 `fresh_idx` 语义）建议对照
   [bilibili-API-collect](https://github.com/SocialSisterYi/bilibili-API-collect) 使用。
