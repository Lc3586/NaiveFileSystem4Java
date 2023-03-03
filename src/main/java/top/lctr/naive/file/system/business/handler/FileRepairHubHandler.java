package top.lctr.naive.file.system.business.handler;

import org.atmosphere.cpr.AtmosphereResource;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import project.extension.hub.HubHandler;
import project.extension.hub.MessageType;
import project.extension.tuple.Tuple2;
import top.lctr.naive.file.system.config.FileRepairConfig;

/**
 * 文件信息修复模块集线器
 *
 * @author LCTR
 * @date 2022-12-08
 */
@Component
public class FileRepairHubHandler
        extends HubHandler {
    public FileRepairHubHandler(FileRepairConfig config) {
        super("文件信息修复模块集线器",
              config.getHubConfig()
                    .getHandlerThreadPoolSize(),
              config.getHubConfig()
                    .getSenderThreadPoolSize(),
              LoggerFactory.getLogger(FileRepairHubHandler.class));
    }

    @Override
    protected Tuple2<MessageType, Object> handlerMessage(AtmosphereResource client,
                                                         Object message) {
        return new Tuple2<>(MessageType.STRING,
                            "");
    }
}
