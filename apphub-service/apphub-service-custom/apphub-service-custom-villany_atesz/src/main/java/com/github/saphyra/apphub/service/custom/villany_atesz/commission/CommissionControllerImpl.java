package com.github.saphyra.apphub.service.custom.villany_atesz.commission;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CommissionCartResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CommissionModel;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CommissionView;
import com.github.saphyra.apphub.api.custom.villany_atesz.server.CommissionController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.CartDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao.Commission;
import com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao.CommissionDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao.ContactDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
class CommissionControllerImpl implements CommissionController {
    private final CommissionRequestValidator commissionRequestValidator;
    private final CommissionFactory commissionFactory;
    private final CommissionDao commissionDao;
    private final CommissionToResponseConverter commissionToResponseConverter;
    private final CartDao cartDao;
    private final ContactDao contactDao;
    private final CommissionCartQueryService commissionCartQueryService;
    private final DateTimeUtil dateTimeUtil;

    @Override
    public CommissionModel createOrUpdateCommission(CommissionModel request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create/edit commission {}", accessTokenHeader.getUserId(), request.getCommissionId());

        commissionRequestValidator.validate(request);

        Commission commission = commissionFactory.create(accessTokenHeader.getUserId(), request);
        commissionDao.save(commission);
        commission.setLastUpdate(dateTimeUtil.getCurrentDateTime());

        return commissionToResponseConverter.convert(commission);
    }

    @Override
    public CommissionModel getCommission(UUID commissionId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to get commission {}", accessTokenHeader.getUserId(), commissionId);

        Commission commission = commissionDao.findByIdValidated(commissionId);

        return commissionToResponseConverter.convert(commission);
    }

    @Override
    public CommissionCartResponse getCart(UUID cartId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to get commissionCart for cartId {}", accessTokenHeader.getUserId(), cartId);

        return commissionCartQueryService.getCart(cartId);
    }

    @Override
    public List<CommissionView> deleteCommission(UUID commissionId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete commission {}", accessTokenHeader.getUserId(), commissionId);

        Commission commission = commissionDao.findByIdValidated(commissionId); //Query is needed for uid validation

        commissionDao.delete(commission);

        return getCommissions(accessTokenHeader);
    }

    @Override
    public List<CommissionView> getCommissions(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know their commissions", accessTokenHeader.getUserId());

        return commissionDao.getByUserId(accessTokenHeader.getUserId())
            .stream()
            .map(commission -> CommissionView.builder()
                .commissionId(commission.getCommissionId())
                .contactName(getContactName(commission.getCartId()))
                .lastUpdate(commission.getLastUpdate())
                .build())
            .toList();
    }

    private String getContactName(UUID cartId) {
        return Optional.ofNullable(cartId)
            .flatMap(uuid -> cartDao.findById(cartId))
            .map(cart -> contactDao.findByIdValidated(cart.getContactId()).getName())
            .orElse(null);
    }
}
