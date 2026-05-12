package com.cfd.user.domain;

import java.util.Optional;

/**
 * 用户仓储接口。
 *
 * <p>定义用户资料的持久化操作契约，包括保存、按ID查询和按用户名查询。</p>
 *
 * @author CFD Platform Team
 */
public interface UserRepository {

    /**
     * 保存或更新用户资料。
     *
     * @param profile 待保存的用户资料
     * @return 保存后的用户资料
     */
    UserProfile save(UserProfile profile);

    /**
     * 根据用户ID查询用户资料。
     *
     * @param userId 用户ID
     * @return 包含用户资料的 Optional，不存在时为空
     */
    Optional<UserProfile> findById(String userId);

    /**
     * 根据用户名查询用户资料。
     *
     * @param username 用户名
     * @return 包含用户资料的 Optional，不存在时为空
     */
    Optional<UserProfile> findByUsername(String username);
}
