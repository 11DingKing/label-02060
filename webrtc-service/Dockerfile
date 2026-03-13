# 使用Maven 3.9作为构建镜像
FROM maven:3.9-eclipse-temurin-17 as build

# 配置国内Maven镜像源
RUN mkdir -p /root/.m2 && \
    echo "<settings>" > /root/.m2/settings.xml && \
    echo "  <mirrors>" >> /root/.m2/settings.xml && \
    echo "    <mirror>" >> /root/.m2/settings.xml && \
    echo "      <id>aliyun</id>" >> /root/.m2/settings.xml && \
    echo "      <name>Aliyun Maven</name>" >> /root/.m2/settings.xml && \
    echo "      <url>https://maven.aliyun.com/repository/public</url>" >> /root/.m2/settings.xml && \
    echo "      <mirrorOf>*</mirrorOf>" >> /root/.m2/settings.xml && \
    echo "    </mirror>" >> /root/.m2/settings.xml && \
    echo "  </mirrors>" >> /root/.m2/settings.xml && \
    echo "</settings>" >> /root/.m2/settings.xml

# 设置工作目录
WORKDIR /app

# 复制pom.xml
COPY pom.xml ./

# 复制源代码
COPY src src

# 构建项目
RUN mvn package -DskipTests --no-transfer-progress

# 使用Eclipse Temurin 17作为运行环境
FROM eclipse-temurin:17-jre

# 设置工作目录
WORKDIR /app

# 复制构建好的jar包
COPY --from=build /app/target/webrtc-service-1.0.0.jar app.jar

# 暴露8080端口
EXPOSE 8080

# 启动应用
CMD ["java", "-jar", "app.jar"]