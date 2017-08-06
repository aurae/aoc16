extern crate crypto;

use crypto::md5::Md5;
use crypto::digest::Digest;

fn main() {
    // Initialize inputs
    let door_id = "cxdnnyjw";

    let mut md5 = Md5::new();
    let mut output: Vec<char> = Vec::new();

    for index in 0..std::u64::MAX {
        // Produce & consume the MD5, then reset for new usage
        let input = format!("{}{}", door_id, index);
        md5.input_str(input.as_str());
        let result = md5.result_str();
        md5.reset();

        // Print progress
        if index % 100_000 == 0 {
            println!("index: {}, found {} matches so far...", index, output.len());
        }

        if result.starts_with("00000") {
            // Found a match! Take the sixth character & append it to the password list
            output.push(result.chars().nth(5).unwrap());

            if output.len() == 8 {
                break;
            }
        }
    }

    let password = output.iter()
        .map(|c| c.to_string())
        .collect::<String>();
    println!("Password: '{}'", password);
}
