const Topic = {
    SEARCH: {
        // Server -> Client
        FINISHED: 'finished',
        FAILED: 'failed',

        // Client <-> Server
        START: 'start',

    },
    DISC: {
        // Client -> Server
        SEARCH: 'search',
        PUSH_TO_DB: 'push_to_db',

        // Client <-> Server
        CLEAR: 'clear',

        // Client <- Server
        // Responses that open a Swal dialog
        ALREADY_EXISTS: 'already_exists',
        STILL_MISMATCHES: 'still_mismatches',
        STILL_INDEXING: 'still_indexing',
        PUSHED_TO_DB: 'pushed_to_db',
        NOT_FOUND: 'not_found',
        SUCCESS: 'success',
        FAILED: 'failed',

        // Client <- Server
        SUBMIT_DISC_INFO: 'submit_disc_info'
    },
    SONGS: {
        CLEAR: 'clear',
        UPDATE: 'update',
        USE_DISCOGS: 'use_discogs',
    },

    USER: {
        // Client -> Server
        PING: 'ping',
        LOGIN: 'login',

        // Client <- Server
        NOT_LOGGED_IN: 'not_logged_in',
    },

    DESCRIPTORS: {
        DISC: 'disc',
        SONGS: 'songs',
        USER: 'user',
        SEARCH: 'search'
    }
};

function findTopic(topic) {
    topic = topic.toUpperCase();
    if (Topic[topic]) {
        return topic;
    }
    return null;
}

export {Topic, findTopic};
