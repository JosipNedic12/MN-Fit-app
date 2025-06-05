// functions/eslint.config.js
module.exports = [
  {
    files: ["**/*.js"],
    languageOptions: {
      ecmaVersion: 2022,
      sourceType: "commonjs",
    },
    rules: {
      indent: ["error", 2],
      semi: ["error", "always"],
      "no-unused-vars": "warn",
      "comma-dangle": ["error", "always-multiline"],
      "eol-last": ["error", "always"],
      quotes: ["error", "double"],
    },
  },
];
