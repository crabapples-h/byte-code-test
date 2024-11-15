package cn.crabapples.system.sysUser.dao;

import cn.crabapples.common.dic.DIC;
import cn.crabapples.system.dto.SysUserDTO;
import cn.crabapples.system.sysUser.dao.mybatis.mapper.UserMapper;
import cn.crabapples.system.sysUser.entity.SysUser;
import cn.crabapples.system.sysUser.form.UserForm;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserDAO extends ServiceImpl<UserMapper, SysUser> {

    public IPage<SysUserDTO> findAll(Integer pageIndex, Integer pageSize, UserForm form) {
        Page<SysUser> page = Page.of(pageIndex, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>(SysUser.class)
                .like(!StringUtils.isEmpty(form.getUsername()), SysUser::getUsername, form.getUsername())
                .like(!StringUtils.isEmpty(form.getName()), SysUser::getName, form.getName())
                .like(!StringUtils.isEmpty(form.getMail()), SysUser::getMail, form.getMail())
                .like(!StringUtils.isEmpty(form.getPhone()), SysUser::getPhone, form.getPhone());
        IPage<SysUser> sysUserList = baseMapper.selectPage(page, wrapper);
        List<SysUserDTO> collect = sysUserList.getRecords().stream().map(e -> {
            SysUserDTO dto = new SysUserDTO();
            BeanUtils.copyProperties(e, dto);
            return dto;
        }).collect(Collectors.toList());
        Page<SysUserDTO> dtoPage = new Page<>();
        BeanUtils.copyProperties(sysUserList, dtoPage);
        dtoPage.setRecords(collect);
        return dtoPage;
    }

    public List<SysUserDTO> findAll(UserForm form) {
        return baseMapper.selectList(new QueryWrapper<>(form.toEntity())).stream().map(e -> {
            final SysUserDTO dto = new SysUserDTO();
            BeanUtils.copyProperties(e, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    public SysUser findOne(UserForm form) {
        return getOne(new QueryWrapper<>(form.toEntity()));
    }

    public SysUser findById(String id) {
        return baseMapper.selectById(id);
    }

    public List<SysUser> findByIds(List<String> ids) {
        return baseMapper.selectBatchIds(ids);
    }

    public boolean removeUser(String id) {
        return removeById(id);
    }

    public boolean lockUser(String id) {
        return SysUser.create().selectById(id).setStatus(DIC.USER_LOCK).updateById();
    }

    public boolean unlockUser(String id) {
        return SysUser.create().selectById(id).setStatus(DIC.USER_UNLOCK).updateById();
    }
}
