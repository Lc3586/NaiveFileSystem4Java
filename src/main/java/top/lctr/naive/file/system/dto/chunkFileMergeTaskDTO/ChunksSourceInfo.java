package top.lctr.naive.file.system.dto.chunkFileMergeTaskDTO;

/**
 * 分片文件合并任务业务模型
 * <p>分片来源信息</p>
 *
 * @author LCTR
 * @date 2022-04-06
 */
public class ChunksSourceInfo {
    public ChunksSourceInfo() {

    }

    public ChunksSourceInfo(String md5,
                            Integer specs,
                            Integer total) {
        setMd5(md5);
        setSpecs(specs);
        setTotal(total);
    }

    private String md5;

    private Integer specs;

    private Integer total;

    private java.util.List<ActivityInfo> activities;

    /**
     * 文件md5值
     */
    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    /**
     * 分片规格
     */
    public Integer getSpecs() {
        return specs;
    }

    public void setSpecs(Integer specs) {
        this.specs = specs;
    }

    /**
     * 总数
     */
    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    /**
     * 热度信息
     */
    public java.util.List<ActivityInfo> getActivities() {
        return activities;
    }

    public void setActivities(java.util.List<ActivityInfo> activities) {
        this.activities = activities;
    }
}
