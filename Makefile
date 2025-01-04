# Styling for output
YELLOW := "\e[1;33m"
NC := "\e[0m"
INFO := @sh -c '\
    printf $(YELLOW); \
    echo "=> $$1"; \
    printf $(NC)' VALUE

.SILENT:  # Ignore output of make `echo` command

.PHONY: help  # Show list of available commands with descriptions
help:
	@$(INFO) "Commands:"
	@grep '^.PHONY: .* #' Makefile | sed 's/\.PHONY: \(.*\) # \(.*\)/\1 > \2/' | column -tx -s ">"

.PHONY: dependencies  # Install basic, dev and test deps
dependencies:
	@clojure -P -M:dev:test

.PHONY: outdated  # Just check outdated deps
outdated:
	@clojure -M:outdated

.PHONY: outdated-fix  # Check and fix outdated deps in deps.edn
outdated-fix:
	@clojure -M:outdated --upgrade --force

.PHONY: check  # Run polylith check
check:
	@clojure -M:poly check

.PHONY: test  # Run tests with coverage
test:
	@clojure -M:poly test :all :dev
