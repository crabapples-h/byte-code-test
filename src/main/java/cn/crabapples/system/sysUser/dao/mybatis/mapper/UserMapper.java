package cn.crabapples.system.sysUser.dao.mybatis.mapper;

import cn.crabapples.system.sysUser.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<SysUser> {
}
