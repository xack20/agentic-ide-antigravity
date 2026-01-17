package com.usermanagement.adapter.persistence;

import com.usermanagement.adapter.persistence.document.UserDocument;
import com.usermanagement.adapter.persistence.mapper.UserDocumentMapper;
import com.usermanagement.adapter.persistence.repository.SpringDataUserRepository;
import com.usermanagement.application.port.output.UserRepository;
import com.usermanagement.application.port.output.UserSearchCriteria;
import com.usermanagement.application.port.output.UserSearchResult;
import com.usermanagement.domain.entity.User;
import com.usermanagement.domain.valueobject.Email;
import com.usermanagement.domain.valueobject.Phone;
import com.usermanagement.domain.valueobject.UserId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MongoDB implementation of UserRepository.
 */
@Repository
public class MongoUserRepository implements UserRepository {

    private final SpringDataUserRepository springDataRepo;
    private final UserDocumentMapper mapper;
    private final MongoTemplate mongoTemplate;

    public MongoUserRepository(SpringDataUserRepository springDataRepo,
            UserDocumentMapper mapper,
            MongoTemplate mongoTemplate) {
        this.springDataRepo = springDataRepo;
        this.mapper = mapper;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public User save(User user) {
        UserDocument doc = mapper.toDocument(user);
        UserDocument saved = springDataRepo.save(doc);
        return mapper.toEntity(saved);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return findById(id.getValue());
    }

    @Override
    public Optional<User> findById(String id) {
        return springDataRepo.findById(id).map(mapper::toEntity);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return findByEmail(email.getValue());
    }

    @Override
    public Optional<User> findByEmail(String normalizedEmail) {
        return springDataRepo.findByEmailNormalized(normalizedEmail).map(mapper::toEntity);
    }

    @Override
    public Optional<User> findByPhone(Phone phone) {
        return findByPhone(phone.getValue());
    }

    @Override
    public Optional<User> findByPhone(String normalizedPhone) {
        return springDataRepo.findByPhoneNormalized(normalizedPhone).map(mapper::toEntity);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return springDataRepo.findByUsername(username).map(mapper::toEntity);
    }

    @Override
    public boolean existsByEmail(String normalizedEmail) {
        return springDataRepo.existsByEmailNormalized(normalizedEmail);
    }

    @Override
    public boolean existsByPhone(String normalizedPhone) {
        return springDataRepo.existsByPhoneNormalized(normalizedPhone);
    }

    @Override
    public boolean existsByUsername(String username) {
        return springDataRepo.existsByUsername(username);
    }

    @Override
    public void deleteById(UserId id) {
        springDataRepo.deleteById(id.getValue());
    }

    @Override
    public UserSearchResult search(UserSearchCriteria criteria) {
        Query query = buildSearchQuery(criteria);

        // Count total
        long totalCount = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), UserDocument.class);

        // Apply pagination
        Sort sort = Sort.by(
                criteria.getSortDirection() == UserSearchCriteria.SortDirection.ASC
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC,
                criteria.getSortField());
        query.with(PageRequest.of(criteria.getPage(), criteria.getPageSize(), sort));

        List<User> users = mongoTemplate.find(query, UserDocument.class).stream()
                .map(mapper::toEntity)
                .collect(Collectors.toList());

        return new UserSearchResult(users, totalCount, criteria.getPage(), criteria.getPageSize());
    }

    @Override
    public long count(UserSearchCriteria criteria) {
        Query query = buildSearchQuery(criteria);
        return mongoTemplate.count(query, UserDocument.class);
    }

    private Query buildSearchQuery(UserSearchCriteria criteria) {
        Query query = new Query();

        // Search query (text search on name, email, phone)
        if (criteria.getSearchQuery() != null && !criteria.getSearchQuery().isBlank()) {
            String searchRegex = ".*" + criteria.getSearchQuery() + ".*";
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("fullNameNormalized").regex(searchRegex, "i"),
                    Criteria.where("emailNormalized").regex(searchRegex, "i"),
                    Criteria.where("phoneNormalized").regex(searchRegex, "i"));
            query.addCriteria(searchCriteria);
        }

        // Status filter
        if (criteria.getStatuses() != null && !criteria.getStatuses().isEmpty()) {
            List<String> statusStrings = criteria.getStatuses().stream()
                    .map(Enum::name)
                    .collect(Collectors.toList());
            query.addCriteria(Criteria.where("status").in(statusStrings));
        }

        // Role filter
        if (criteria.getRoleIds() != null && !criteria.getRoleIds().isEmpty()) {
            query.addCriteria(Criteria.where("roleIds").in(criteria.getRoleIds()));
        }

        // Email verified filter
        if (criteria.getEmailVerified() != null) {
            query.addCriteria(Criteria.where("emailVerified").is(criteria.getEmailVerified()));
        }

        // Date range
        if (criteria.getCreatedAfter() != null) {
            query.addCriteria(Criteria.where("createdAt").gte(criteria.getCreatedAfter()));
        }
        if (criteria.getCreatedBefore() != null) {
            query.addCriteria(Criteria.where("createdAt").lte(criteria.getCreatedBefore()));
        }

        return query;
    }
}
