package edu.hitsz.scores;

import java.util.List;

/**
 * 分数记录数据访问对象接口
 */
public interface ScoreDao {
    /**
     * 保存分数记录列表
     * @param records 分数记录列表
     */
    void saveScores(List<ScoreRecord> records);

    /**
     * 加载分数记录列表
     * @return 分数记录列表
     */
    List<ScoreRecord> loadScores();
}