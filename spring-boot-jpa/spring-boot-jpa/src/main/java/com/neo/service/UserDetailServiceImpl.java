package com.neo.service;

import com.mysql.cj.util.StringUtils;
import com.neo.model.UserDetail;
import com.neo.param.UserDetailParam;
import com.neo.repository.UserDetailRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
// impl和接口分开，不过最好还是专门整一个impl的package
@Service
public class UserDetailServiceImpl implements  UserDetailService{

    @Resource // 自动装载bean 也可以可以使用@Autowired
    private UserDetailRepository userDetailRepository;

    @Override
    public Page<UserDetail> findByCondition(UserDetailParam detailParam, Pageable pageable){
        // 复杂查询使用Criteria 接口，对应root CriteriaBuilder，CriteriaQuery   https://banbanpeppa.github.io/2019/01/25/java/spring/spring-jpa-muti-query/
        // Predicate 配合 Lambda 和 函数式编程使用 类似于一种限制条件 https://blog.csdn.net/qazzwx/article/details/107864622
        return userDetailRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();
            //equal 示例
            if (!StringUtils.isNullOrEmpty(detailParam.getIntroduction())){
                predicates.add(cb.equal(root.get("introduction"),detailParam.getIntroduction()));
            }
            //like 示例
            if (!StringUtils.isNullOrEmpty(detailParam.getRealName())){
                predicates.add(cb.like(root.get("realName"),"%"+detailParam.getRealName()+"%"));
            }
            //between 示例
            if (detailParam.getMinAge()!=null && detailParam.getMaxAge()!=null) {
                Predicate agePredicate = cb.between(root.get("age"), detailParam.getMinAge(), detailParam.getMaxAge());
                predicates.add(agePredicate);
            }
            //greaterThan 大于等于示例
            if (detailParam.getMinAge()!=null){
                predicates.add(cb.greaterThan(root.get("age"),detailParam.getMinAge()));
            }
            return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
        }, pageable);

    }
}
