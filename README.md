<h1 align="center">Theresa</h1>
<h4 align="center">基于Mixin的客户端</h4>

## JVM参数

    -Dfml.coreMods.load=cn.loli.client.injection.MixinLoader

### 导入与导出

#### 导入
IDEA

    ./gradlew setupDevWorkspace idea genIntellijRuns
    
Eclipse
    
    ./gradlew setupDevWorkspace eclipse
#### 导出
不清理build文件夹导出

    ./gradlew build

清理build文件夹导出

    ./gradlew clean build
