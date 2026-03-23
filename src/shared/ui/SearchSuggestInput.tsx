interface SearchSuggestInputProps {
  label: string
  placeholder: string
  suggestion: string | null
  value: string
  onChange: (value: string) => void
  onAcceptSuggestion: (value: string) => void
}

export function SearchSuggestInput({
  label,
  placeholder,
  suggestion,
  value,
  onChange,
  onAcceptSuggestion,
}: SearchSuggestInputProps) {
  const suggestionSuffix =
    suggestion && suggestion.toLocaleLowerCase('ru').startsWith(value.toLocaleLowerCase('ru'))
      ? suggestion.slice(value.length)
      : ''

  return (
    <label className="field field--search">
      <span className="field__label">{label}</span>

      <div className="search-input">
        {suggestionSuffix ? (
          <div aria-hidden="true" className="search-input__ghost">
            <span className="search-input__ghost-typed">{value}</span>
            <span className="search-input__ghost-suggestion">{suggestionSuffix}</span>
          </div>
        ) : null}

        <input
          className="field__control search-input__control"
          onChange={(event) => onChange(event.target.value)}
          onKeyDown={(event) => {
            if (event.key === 'Tab' && suggestion) {
              event.preventDefault()
              onAcceptSuggestion(suggestion)
            }
          }}
          placeholder={placeholder}
          type="search"
          value={value}
        />
      </div>

      <span className="field__hint">
        {suggestion ? `Tab: ${suggestion}` : 'Ищите по названию экскурсии или точки'}
      </span>
    </label>
  )
}
