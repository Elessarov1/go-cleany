\set ON_ERROR_STOP on

begin;

-- Break the two intentional reservation cycles before deleting their operational rows.
update cleaning_order
   set applied_reward_id = null,
       applied_rental_cleaning_benefit_id = null;

delete from transaction_feedback;
delete from support_case;
delete from cleaning_order_issue_photo;
delete from cleaning_order_issue_report;
delete from cleaning_order_photo;
delete from cleaning_order_event;
delete from partner_payout;
delete from referral_reward;
delete from rental_cleaning_benefit;
delete from rental_transfer_benefit;
delete from rental_occupancy where type = 'BOOKING';
delete from rental_booking;
delete from transfer_booking;
delete from cleaning_order;

-- Operational Cleaning media is everything not owned by the preserved Rental catalog.
delete from media_provider_reference provider_reference
 where not exists (
     select 1
       from rental_property_media property_media
      where property_media.media_asset_id = provider_reference.media_asset_id
 );
delete from media_asset asset
 where not exists (
     select 1
       from rental_property_media property_media
      where property_media.media_asset_id = asset.id
 );

delete from customer_notification;
delete from customer_identity_link_request;
delete from customer_acquisition;
delete from acquisition_campaign_entry;
delete from referral_code where customer_id is not null;

-- ADMIN is a persisted CustomerAccount role. Keep those accounts, identities, roles and
-- notification preferences so the platform remains administrable immediately after reset.
delete from customer_account account
 where not exists (
     select 1
       from customer_role role
      where role.customer_id = account.id
        and role.role = 'ADMIN'
 );

delete from spring_session_attributes;
delete from spring_session;

-- Reset only sequences whose owning tables are now guaranteed to be empty.
select setval(pg_get_serial_sequence('cleaning_order_issue_photo', 'id'), 1, false);
select setval(pg_get_serial_sequence('cleaning_order_issue_report', 'id'), 1, false);
select setval(pg_get_serial_sequence('cleaning_order_photo', 'id'), 1, false);
select setval(pg_get_serial_sequence('cleaning_order_event', 'id'), 1, false);
select setval(pg_get_serial_sequence('partner_payout', 'id'), 1, false);
select setval(pg_get_serial_sequence('referral_reward', 'id'), 1, false);
select setval(pg_get_serial_sequence('rental_cleaning_benefit', 'id'), 1, false);
select setval(pg_get_serial_sequence('rental_booking', 'id'), 1, false);
select setval(pg_get_serial_sequence('transfer_booking', 'id'), 1, false);
select setval(pg_get_serial_sequence('cleaning_order', 'id'), 1, false);
select setval(pg_get_serial_sequence('customer_notification', 'id'), 1, false);
select setval(pg_get_serial_sequence('transaction_feedback', 'id'), 1, false);
select setval(pg_get_serial_sequence('support_case', 'id'), 1, false);
select setval(pg_get_serial_sequence('customer_identity_link_request', 'id'), 1, false);
select setval(pg_get_serial_sequence('acquisition_campaign_entry', 'id'), 1, false);

commit;
