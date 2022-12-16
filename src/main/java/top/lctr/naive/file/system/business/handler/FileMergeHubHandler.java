package top.lctr.naive.file.system.business.handler;

import org.atmosphere.cpr.AtmosphereResource;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import project.extension.hub.HubHandler;
import project.extension.hub.MessageType;
import project.extension.tuple.Tuple2;
import top.lctr.naive.file.system.config.UploadLargeFileConfig;

/**
 * 分片文件合并集线器处理类
 *
 * @author LCTR
 * @date 2022-12-08
 */
@Component
public class FileMergeHubHandler
        extends HubHandler {
    public FileMergeHubHandler(UploadLargeFileConfig config) {
        super("分片文件合并集线器处理类",
              config.getHubConfig()
                    .getHandlerThreadPoolSize(),
              config.getHubConfig()
                    .getSenderThreadPoolSize(),
              LoggerFactory.getLogger(FileMergeHubHandler.class));
    }

    @Override
    protected Tuple2<MessageType, Object> handlerMessage(AtmosphereResource client,
                                                         Object data) {
        return new Tuple2<>(MessageType.STRING,
                            "");
    }
}
