CREATE TABLE collection (
    id SERIAL PRIMARY KEY NOT NULL,
    finalized boolean NOT NULL DEFAULT false,
    name text NOT NULL
);

CREATE TABLE package (
    id SERIAL PRIMARY KEY NOT NULL,
    collection_id integer NOT NULL REFERENCES collection(id) ON DELETE CASCADE,
    name text NOT NULL
);

CREATE TABLE file_artifact (
    id SERIAL PRIMARY KEY NOT NULL,
    package_id integer NOT NULL REFERENCES package(id) ON DELETE CASCADE,
    path text NOT NULL
);

CREATE TABLE class_entry (
    id SERIAL PRIMARY KEY NOT NULL,
    collection_id integer NOT NULL REFERENCES collection(id) ON DELETE CASCADE, -- denormalized
    file_artifact_id integer NOT NULL REFERENCES file_artifact(id) ON DELETE CASCADE,
    class_name text NOT NULL,
    namespace text
);

CREATE TABLE manifest_entry (
    id SERIAL PRIMARY KEY NOT NULL,
    collection_id integer NOT NULL REFERENCES collection(id) ON DELETE CASCADE, -- denormalized
    file_artifact_id integer NOT NULL REFERENCES file_artifact(id) ON DELETE CASCADE,
    key text NOT NULL,
    value text NOT NULL
);


CREATE INDEX package__collection_id ON package(collection_id);
CREATE INDEX file_artifact__package_id ON file_artifact (package_id);
CREATE INDEX class_entry__file_artifact_id ON class_entry(file_artifact_id);
CREATE INDEX manifest_entry__file_artifact_id ON manifest_entry (file_artifact_id);

CREATE INDEX manifest_entry__key ON manifest_entry(lower(key));
CREATE INDEX class_entry__name ON class_entry(collection_id, lower(class_name));
CREATE INDEX class_entry__name__compound ON class_entry(collection_id, lower(namespace||'.'||class_name));
