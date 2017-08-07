extern crate common;

use std::collections::btree_map::BTreeMap;
use common::files::read;

fn main() {
    // Read & process inputs (transform list of rows to columns)
    let content = read("assets/input.txt");
    let line_len = content.lines().nth(0).unwrap().len();
    let letters = content.lines()
        .flat_map(|l| l.chars())
        .map(|c| c.to_string())
        .collect::<Vec<String>>();

    // Each item in this Vec contains the letters at this position
    let mut columns: Vec<Vec<String>> = vec![Vec::new(); line_len];

    for (i, letter) in letters.iter().enumerate() {
        let column_index = i % line_len;
        columns[column_index].push(letter.to_string());
    }

    let mut count: BTreeMap<String, i32> = BTreeMap::new();
    for column in columns {
        for l in column {
            *count.entry(l).or_insert(0) += 1;
        }

        let mut results = count.iter().last();
//        results.sort_by(|a, b| a.0.cmp(b.0));
        println!("Results of column: {:?}", results);

        count.clear();
    }
}
