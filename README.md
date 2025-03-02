# ServerRemoteExecution
----
ServerRemoteExecution，基于SpringBoot3的服务器远程维护工具，支持查看日志(<10m)和列出文件，下载文件等操作，也支持远程执行白名单的powershell命令，web容器为Undertow(非默认tomcat)，其他的请自行添加和配置。

*.无DB连接型，审计操作请从log文件中提取。 支持Windows Only(目前)。

*.Powered by **Moshow郑锴**@https://zhengkai.blog.csdn.net/

# 功能
- ### 文件操作
    - List
    - Read
    - Download
- ### 命令执行(Powershell)
    - Execute



# 如何运行
1. 首先确保你是JDK17(SpringBoot2.7需要JDK11,SpringBoot3需要JDK17)，推荐微软的MSJDK17
2. 使用Maven安装类库，国内推荐使用阿里云Maven镜像，`mvn clean compile`
3. 项目已配置新版的SpringBoot3.3，需要2.7X版本请从SpringBoot2分支中提取
4. 找到Application，运行项目
5. 访问`http://localhost:12306/sre/` ，会显示`hello world - https://zhengkai.blog.csdn.net/`



TEST SHELL
----
- `Get-Item .`
- `Get-Item .`

版本更新
----
| 版本         | 更新内容                                       |
|------------|--------------------------------------------|
| 2025-03-02 | initial version                            |

预览
----
![SHELL](img1.png)
![FILE](img2.png) 
![img.png](img3.png)


List folder
----
Visit `http://localhost:12306/sre/list` to list folder.

Request Body
```json
{
  "filePath":"D:\\Download\\",
  "userName":"admin"
}
```

Return Body
```json
[
  {
    "directory": false,
    "lastModified": "2025-03-02 20:45:53",
    "name": "removeYoudao.js",
    "path": "D:\\Download\\removeYoudao.js",
    "size": "0"
  }
]
```



Read Log (Size<10m) | Download File
----
**Visit** `http://localhost:12306/sre/read` to read log or `http://localhost:12306/sre/download` to download the file.

**Request Body**
```json
{
 "filePath":"D:\\Download\\removeYoudao.js",
 "userName":"admin"
}
```

**Response Body**
```text
NOT A JSON Object BUT YOUR FILE CONTENT
```

Execute Command
----
Visit `http://localhost:12306/sre/execute` to execute command.

**Request Body**
```json
{
  "command":"Get-Item .",
  "userName":"admin"
}
```

**Response Body**
```text
    Ŀ¼: D:\Workspace\Project


Mode                 LastWriteTime         Length Name                                                                 
----                 -------------         ------ ----                                                                 
d-----          2025/3/3      0:14                ServerRemoteExecution                                                
```