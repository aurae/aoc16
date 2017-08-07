use std::fs::File;
use std::io::Read;

/**
 * Fully reads the contents of the given File at the provided path,
 * and returns those contents as a String.
 */
pub fn read(path: &str) -> String {
    let mut file = File::open(path).unwrap();
    let mut content = String::new();
    file.read_to_string(&mut content).unwrap();
    return content;
}
