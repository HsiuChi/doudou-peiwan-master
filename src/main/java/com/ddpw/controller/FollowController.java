package com.ddpw.controller;


import com.ddpw.dto.Result;
import com.ddpw.service.IFollowService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 关注管理
 *
 * @author zxq
 * @since 2023-11-2
 */
@RestController
@RequestMapping("/follow")
public class FollowController {

    @Resource
    private IFollowService followService;

    /**
     * 关注和取关
     */
    @PutMapping("/{id}/{isFollow}")
    public Result follow(@PathVariable("id") Long followUserId, @PathVariable("isFollow") Boolean isFollow) {

        return followService.follow(followUserId, isFollow);
    }

    /**
     * 判断是否关注
     */
    @GetMapping("/or/not/{id}")
    public Result isFollow(@PathVariable("id") Long followUserId) {

        return followService.isFollow(followUserId);
    }

    /**
     * 求 传入id用户 与 当前登录用户 的共同关注列表
     */
    @GetMapping("/common/{id}")
    public Result followCommons(@PathVariable("id") Long id) {

        return followService.followCommons(id);
    }
}
