use jni::errors::{Error, Exception, ToException};
use jni::JNIEnv;

pub(super) enum SurrealError {
    Exception(Error),
    NullPointerException(&'static str),
    NoSuchElementException,
    SurrealDB(surrealdb::Error),
}

const EXCEPTION: &str = "java/lang/exception";
const NULL_POINTER_EXCEPTION: &str = "java/lang/NullPointerException";
const NO_SUCH_ELEMENT_EXCEPTION: &str = "java/util/NoSuchElementException";
const SURREALDB_EXCEPTION: &str = "com/surrealdb/SurrealDBException";

impl ToException for SurrealError {
    fn to_exception(&self) -> Exception {
        match self {
            Self::Exception(e) => Exception { class: EXCEPTION.to_string(), msg: format!("{e}") },
            Self::NullPointerException(s) => Exception { class: NULL_POINTER_EXCEPTION.to_string(), msg: format!("{s} instance not found") },
            Self::NoSuchElementException => Exception { class: NO_SUCH_ELEMENT_EXCEPTION.to_string(), msg: "No more elements".to_string() },
            Self::SurrealDB(e) => Exception { class: SURREALDB_EXCEPTION.to_string(), msg: format!("{e}") }
        }
    }
}

impl SurrealError {
    pub(super) fn exception<T, F: FnOnce() -> T>(self, env: &mut JNIEnv, output: F) -> T {
        if let Ok(b) = env.exception_check() {
            // If there is already an exception thrown we don't add one
            if !b {
                let _ = env.throw(self.to_exception());
            }
        }
        output()
    }
}

impl From<Error> for SurrealError {
    fn from(e: Error) -> Self {
        SurrealError::Exception(e)
    }
}

impl From<surrealdb::Error> for SurrealError {
    fn from(e: surrealdb::Error) -> Self {
        SurrealError::SurrealDB(e)
    }
}