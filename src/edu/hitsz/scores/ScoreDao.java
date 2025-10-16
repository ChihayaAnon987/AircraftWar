package edu.hitsz.scores;

import java.util.List;

/**
 * 分数记录数据访问对象接口
 */
public interface ScoreDao {
    /**
     * 保存分数记录列表
     * @param records 分数记录列表
     * @param level 难度等级
     */
    void saveScores(List<ScoreRecord> records, int level);

    /**
     * 加载分数记录列表
     * @param level 难度等级
     * @return 分数记录列表
     */
    List<ScoreRecord> loadScores(int level);
}