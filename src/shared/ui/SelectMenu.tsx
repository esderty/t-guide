import { useEffect, useId, useRef, useState } from 'react'

export interface SelectMenuOption<T extends string> {
  value: T
  label: string
}

interface SelectMenuProps<T extends string> {
  label: string
  options: SelectMenuOption<T>[]
  value: T
  onChange: (value: T) => void
}

export function SelectMenu<T extends string>({
  label,
  options,
  value,
  onChange,
}: SelectMenuProps<T>) {
  const [isOpen, setIsOpen] = useState(false)
  const rootRef = useRef<HTMLDivElement | null>(null)
  const listboxId = useId()
  const activeOption = options.find((option) => option.value === value)

  useEffect(() => {
    function handleOutsideClick(event: MouseEvent) {
      if (!rootRef.current?.contains(event.target as Node)) {
        setIsOpen(false)
      }
    }

    function handleEscape(event: KeyboardEvent) {
      if (event.key === 'Escape') {
        setIsOpen(false)
      }
    }

    window.addEventListener('mousedown', handleOutsideClick)
    window.addEventListener('keydown', handleEscape)

    return () => {
      window.removeEventListener('mousedown', handleOutsideClick)
      window.removeEventListener('keydown', handleEscape)
    }
  }, [])

  return (
    <div className={`field field--menu${isOpen ? ' field--menu-open' : ''}`} ref={rootRef}>
      <span className="field__label">{label}</span>

      <button
        aria-controls={listboxId}
        aria-expanded={isOpen}
        aria-haspopup="listbox"
        className="field__control field__control--button"
        onClick={() => setIsOpen((open) => !open)}
        type="button"
      >
        <span>{activeOption?.label}</span>
        <span className={`field__chevron${isOpen ? ' field__chevron--open' : ''}`}>
          ▾
        </span>
      </button>

      <div
        aria-hidden={!isOpen}
        className={`field__menu${isOpen ? ' field__menu--open' : ''}`}
      >
        <div
          aria-label={label}
          className="field__menu-list"
          id={listboxId}
          role="listbox"
        >
          {options.map((option) => (
            <button
              aria-selected={option.value === value}
              className={`field__menu-option${option.value === value ? ' field__menu-option--active' : ''}`}
              key={option.value}
              onClick={() => {
                onChange(option.value)
                setIsOpen(false)
              }}
              role="option"
              type="button"
            >
              {option.label}
            </button>
          ))}
        </div>
      </div>
    </div>
  )
}
