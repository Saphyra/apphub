package com.github.saphyra.apphub.service.custom.villany_atesz.commission;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CommissionCartResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CommissionModel;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CommissionView;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.Cart;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.CartDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao.Commission;
import com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao.CommissionDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao.Contact;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao.ContactDao;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CommissionControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID COMMISSION_ID = UUID.randomUUID();
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final UUID CART_ID = UUID.randomUUID();
    private static final UUID CONTACT_ID = UUID.randomUUID();
    private static final String CONTACT_NAME = "contact-name";

    @Mock
    private CommissionRequestValidator commissionRequestValidator;

    @Mock
    private CommissionFactory commissionFactory;

    @Mock
    private CommissionDao commissionDao;

    @Mock
    private CommissionToResponseConverter commissionToResponseConverter;

    @Mock
    private CartDao cartDao;

    @Mock
    private ContactDao contactDao;

    @Mock
    private CommissionCartQueryService commissionCartQueryService;

    @InjectMocks
    private CommissionControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private Commission commission1;

    @Mock
    private Commission commission2;

    @Mock
    private CommissionModel response;

    @Mock
    private CommissionModel request;

    @Mock
    private Cart cart;

    @Mock
    private Contact contact;

    @Mock
    private CommissionCartResponse commissionCartResponse;

    @Test
    void createOrUpdateCommission() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(commissionFactory.create(USER_ID, request)).willReturn(commission1);
        given(commissionDao.saveAndReturn(commission1)).willReturn(commission2);
        given(commissionToResponseConverter.convert(commission2)).willReturn(response);

        assertThat(underTest.createOrUpdateCommission(request, accessTokenHeader)).isEqualTo(response);

        then(commissionRequestValidator).should().validate(request);
    }

    @Test
    void getCommission() {
        given(commissionDao.findByIdValidated(COMMISSION_ID)).willReturn(commission1);
        given(commissionToResponseConverter.convert(commission1)).willReturn(response);

        assertThat(underTest.getCommission(COMMISSION_ID, accessTokenHeader)).isEqualTo(response);
    }

    @Test
    void getCommissions_noCartId() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(commissionDao.getByUserId(USER_ID)).willReturn(List.of(commission1));
        given(commission1.getCommissionId()).willReturn(COMMISSION_ID);
        given(commission1.getCartId()).willReturn(null);
        given(commission1.getLastUpdate()).willReturn(LAST_UPDATE);

        CustomAssertions.singleListAssertThat(underTest.getCommissions(accessTokenHeader))
            .returns(COMMISSION_ID, CommissionView::getCommissionId)
            .returns(null, CommissionView::getContactName)
            .returns(LAST_UPDATE, CommissionView::getLastUpdate);
    }

    @Test
    void getCommissions_cartNotFound() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(commissionDao.getByUserId(USER_ID)).willReturn(List.of(commission1));
        given(commission1.getCommissionId()).willReturn(COMMISSION_ID);
        given(commission1.getCartId()).willReturn(CART_ID);
        given(cartDao.findById(CART_ID)).willReturn(Optional.empty());
        given(commission1.getLastUpdate()).willReturn(LAST_UPDATE);

        CustomAssertions.singleListAssertThat(underTest.getCommissions(accessTokenHeader))
            .returns(COMMISSION_ID, CommissionView::getCommissionId)
            .returns(null, CommissionView::getContactName)
            .returns(LAST_UPDATE, CommissionView::getLastUpdate);
    }

    @Test
    void getCommissions_withContact() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(commissionDao.getByUserId(USER_ID)).willReturn(List.of(commission1));
        given(commission1.getCommissionId()).willReturn(COMMISSION_ID);
        given(commission1.getCartId()).willReturn(CART_ID);
        given(cartDao.findById(CART_ID)).willReturn(Optional.of(cart));
        given(cart.getContactId()).willReturn(CONTACT_ID);
        given(contactDao.findByIdValidated(CONTACT_ID)).willReturn(contact);
        given(contact.getName()).willReturn(CONTACT_NAME);
        given(commission1.getLastUpdate()).willReturn(LAST_UPDATE);

        CustomAssertions.singleListAssertThat(underTest.getCommissions(accessTokenHeader))
            .returns(COMMISSION_ID, CommissionView::getCommissionId)
            .returns(CONTACT_NAME, CommissionView::getContactName)
            .returns(LAST_UPDATE, CommissionView::getLastUpdate);
    }

    @Test
    void deleteCommission() {
        given(commissionDao.findByIdValidated(COMMISSION_ID)).willReturn(commission1);
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(commissionDao.getByUserId(USER_ID)).willReturn(List.of(commission1));
        given(commission1.getCommissionId()).willReturn(COMMISSION_ID);
        given(commission1.getCartId()).willReturn(CART_ID);
        given(cartDao.findById(CART_ID)).willReturn(Optional.empty());
        given(commission1.getLastUpdate()).willReturn(LAST_UPDATE);

        CustomAssertions.singleListAssertThat(underTest.deleteCommission(COMMISSION_ID, accessTokenHeader))
            .returns(COMMISSION_ID, CommissionView::getCommissionId)
            .returns(null, CommissionView::getContactName)
            .returns(LAST_UPDATE, CommissionView::getLastUpdate);

        then(commissionDao).should().delete(commission1);
    }

    @Test
    void getCart() {
        given(commissionCartQueryService.getCart(CART_ID)).willReturn(commissionCartResponse);

        assertThat(underTest.getCart(CART_ID, accessTokenHeader)).isEqualTo(commissionCartResponse);
    }
}