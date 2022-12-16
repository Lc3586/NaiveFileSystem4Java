package top.lctr.naive.file.system.business.handler;

import org.atmosphere.cpr.AtmosphereResource;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import project.extension.hub.HubHandler;
import project.extension.hub.MessageType;
import project.extension.tuple.Tuple2;
import top.lctr.naive.file.system.config.FileRepairConfig;

/**
 * Word文件自动转换Pdf文件集线器处理类
 *
 * @author LCTR
 * @date 2022-12-08
 */
@Component
public class Word2PdfHubHandler
        extends HubHandler {
    public Word2PdfHubHandler(FileRepairConfig config) {
        super("Word文件自动转换Pdf文件集线器处理类",
              config.getHubConfig()
                    .getHandlerThreadPoolSize(),
              config.getHubConfig()
                    .getSenderThreadPoolSize(),
              LoggerFactory.getLogger(Word2PdfHubHandler.class));
    }

    @Override
    protected Tuple2<MessageType, Object> handlerMessage(AtmosphereResource client,
                                                         Object message) {
        return new Tuple2<>(MessageType.STRING,
                            "");
    }
}
