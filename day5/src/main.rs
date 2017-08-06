extern crate crypto;

use crypto::md5::Md5;
use crypto::digest::Digest;

fn create_password(chars: &Vec<char>) -> String {
    return chars.iter()
        .map(|c| {
            let str_value = c.to_string();
            if str_value == "_" {
                return "_".to_string();
            } else {
                return str_value;
            }
        })
        .collect::<String>();
}

fn part_one(door_id: &str) {
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

fn part_two(door_id: &str) {
    let mut md5 = Md5::new();
    let mut output: [char; 8] = ['_'; 8];

    for index in 0..std::u64::MAX {
        // Produce & consume the MD5, then reset for new usage
        let input = format!("{}{}", door_id, index);
        md5.input_str(input.as_str());
        let result = md5.result_str();
        md5.reset();

        // Print progress
        if index % 100_000 == 0 {
            println!("index: {}...", index);
        }

        if result.starts_with("00000") {
            // Found a match! Check the value of the sixth character
            // & if valid, push the seventh character to the result array
            // at the position indicated by the sixth
            let position = result.chars()
                .nth(5)
                .unwrap()
                .to_string()
                .parse::<i8>();


            match position {
                Ok(n) => {
                    if n >= 0 && n < 8 {
                        // Valid array index, check if no item at that position yet
                        println!("Found a potential match at index {} (hash={}). Position: {:?}", index, result, n);

                        let current_value = output.get(n as usize).unwrap().clone();
                        if current_value == '_' {
                            // Empty slot found; set its value
                            output[n as usize] = result.chars().nth(6).unwrap();
                            println!("Password: '{}' | (found match @ index {} w/ hash {})",
                                     create_password(&output.to_vec()), index, result);

                            // Check if anything more needs to be done
                            let undecided_count = output.iter()
                                .filter(|c| **c == '_')
                                .count();

                            if undecided_count == 0 {
                                // All done.
                                break;
                            }

                        } else {
                            println!("Position {} is already filled. Continue...");
                        }
                    }
                }
                Err(_) => ()
            }
        }
    }

    println!("Password: '{}'", create_password(&output.to_vec()));
}

fn main() {
    // Initialize inputs
    let door_id = "cxdnnyjw";
    part_two(door_id);
}
