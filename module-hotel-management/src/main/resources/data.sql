INSERT INTO hotels (name) VALUES('Общага');

INSERT INTO rooms (number, hotel_id, available, times_booked) VALUES('313', (SELECT id FROM hotels WHERE name = 'Общага'), true, 0);
INSERT INTO ROOMS (number, hotel_id, available, times_booked) VALUES('314', (SELECT id FROM hotels WHERE name = 'Общага'), true, 0);