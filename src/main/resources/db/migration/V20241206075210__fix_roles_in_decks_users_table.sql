UPDATE decks_users 
SET role = 'OWNER' 
WHERE user_id IN (SELECT owner_id FROM decks WHERE decks.id = decks_users.deck_id);