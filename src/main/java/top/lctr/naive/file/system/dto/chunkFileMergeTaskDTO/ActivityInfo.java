package top.lctr.naive.file.system.dto.chunkFileMergeTaskDTO;

/**
 * 分片文件合并任务业务模型
 * <p>热度信息</p>
 *
 * @author LCTR
 * @date 2022-04-06
 */
public class ActivityInfo {
    public ActivityInfo() {

    }

    public ActivityInfo(Integer activity,
                        Double percentage) {
        setActivity(activity);
        setPercentage(percentage);
    }

    private Integer activity;

    private Double percentage;

    /**
     * 热度
     */
    public Integer getActivity() {
        return activity;
    }

    public void setActivity(Integer activity) {
        this.activity = activity;
    }

    /**
     * 百分比
     */
    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }
}
