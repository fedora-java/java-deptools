INSERT INTO packagecollection(id, name, finalized) VALUES
    (1, 'test', true),
    (2, 'test2', true);
INSERT INTO package(packagecollectionid, id, name) VALUES
    (1, 1, 'rnv'),
    (1, 2, 'eclipse'),
    (2, 3, 'freemind');