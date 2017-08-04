extern crate common;

use std::cmp::Ordering;
use common::files::read;

fn main() {
    // Read & prepare input
    let content = read("assets/input.txt");
    let lines: Vec<&str> = content.lines().collect();

    let mut total_sum = 0;
    for line in lines {
        // Split off each line's checksum & sector ID first,
        // then prepare the text content itself
        // (Example: "irgyyolokj-xghhoz-lotgtiotm-228[vnmxd]")
        let cs_start = line.find('[').unwrap();
        let cs_end = line.find(']').unwrap();
        let checksum: &str = &line[cs_start + 1..cs_end];

        let sector_start = line.rfind('-').unwrap();
        let sector_id: &i32 = &line[sector_start + 1..cs_start]
            .to_string()
            .parse::<i32>()
            .unwrap();

        let room_name: &str = &line[..sector_start]
            .to_string()
            .replace("-", "");

        // Compute the N most common letters of the room name in 3 steps,
        // where N is the length of the checksum to compare against:
        // 1) Count each letter's occurrences in the room name
        let mut chars: Vec<(usize, char)> = room_name.chars()
            .map(|c| (room_name.split(c).count() - 1, c))
            .collect();

        // 2) Sort the inputs so that the most common letters are upfront,
        // and ties are sorted alphabetically
        chars.sort_by(|val1, val2| {
            let &(c1, i1) = val1;
            let &(c2, i2) = val2;

            return match c2.cmp(&c1) {
                Ordering::Equal => i1.cmp(&i2),
                n => n
            };
        });

        // 3) Remove duplicated values (1 letter per list only)
        chars.dedup_by(|val1, val2| val1.1 == val2.1);

        // Finally, compose the checksum by aggregating the values in this list into a string
        let compare_list: Vec<String> = chars.into_iter()
            .map(|val| val.1.to_string())
            .take(checksum.len())
            .collect();
        let compare_sum = compare_list.join("");

        // Add the sector ID of valid rooms to the total
        if checksum == compare_sum {
            total_sum += *sector_id;
        }
    }

    println!("Total sum: {}", total_sum);
}
