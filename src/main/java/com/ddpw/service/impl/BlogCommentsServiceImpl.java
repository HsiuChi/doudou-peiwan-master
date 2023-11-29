package com.ddpw.service.impl;

import com.ddpw.entity.BlogComments;
import com.ddpw.mapper.BlogCommentsMapper;
import com.ddpw.service.IBlogCommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zxq
 * @since 2023-11-2
 */
@Service
public class BlogCommentsServiceImpl extends ServiceImpl<BlogCommentsMapper, BlogComments> implements IBlogCommentsService {

}
